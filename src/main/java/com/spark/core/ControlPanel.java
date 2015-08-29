package com.spark.core;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.filters.Canvas;
import net.coobird.thumbnailator.geometry.Positions;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;



@RestController
@ComponentScan
public class ControlPanel {
    
	@Value("${bingappkey}")
	private String bingAppKey;
	
	@Value("${rekognitionapikey}")
	private String rekognitionApiKey;
	
	@Value("${rekognitionapisecret}")
	private String rekognitionApiSecret;
	
	@Value("${clarifaiapiurl}")
	private String clarifaiApiUrl;
	@Value("${clarifaiapikey}")
	private String clarifaiApikey;
	@Value("${clarifaiapisecret}")
	private String clarifaiApiSecret;
	
	@Value("${awsbucketname}")
	private String awsBucketName;
	@Value("${awstagprocessbucketname}")
	private String awsTagProcessBucketName;
	@Autowired
	ImageRepository imageRepo;
	
	@Autowired
	TagRepository tagRepo;
	
	@RequestMapping(method=RequestMethod.GET, value={"/imagesearch"})
	public String generateMedia(@RequestHeader(value="query") List<String> queries, @RequestHeader(value="top") int top) throws Exception{
		
    	AmazonS3Client s3client = new AmazonS3Client(new DefaultAWSCredentialsProviderChain());
    	//		HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
//		RestTemplate restTemplate = new RestTemplate();
//		restTemplate.setRequestFactory(httpRequestFactory);
//		Jaxb2RootElementHttpMessageConverter xmlToPojo = new Jaxb2RootElementHttpMessageConverter();
//		MappingJackson2HttpMessageConverter jsonToPojo = new MappingJackson2HttpMessageConverter();
//        List<MediaType> supportedMediaTypes=new ArrayList<MediaType>();
//        supportedMediaTypes.add(new MediaType("application","atom+xml",Charset.forName("UTF-8")));
//        supportedMediaTypes.add(new MediaType("application","octet-stream",Charset.forName("UTF-8")));
//        supportedMediaTypes.add(new MediaType("application","json",Charset.forName("UTF-8")));
//        jsonToPojo.setSupportedMediaTypes(supportedMediaTypes);
//        jsonToPojo.getObjectMapper().setPropertyNamingStrategy(
//                PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
//        restTemplate.getMessageConverters().add(jsonToPojo);
		
    	String clarifaiAccessToken = null;
        String s3TagProcessUrl = null;
        String hashedName = null;
		for (String query : queries){
	        String requestUrl = "https://api.datamarket.azure.com/Data.ashx/Bing/Search/Image";
	        String requestParams;
	        try {
	            requestParams = "?Query=" +
	            		URLEncoder.encode("'" + query + "'", "UTF-8") + "&ImageFilters=" + URLEncoder.encode("'Size:Large'", "UTF-8") + "&$format=json&$top=" + top;
	        } catch (Exception e){
	        	return null;
	        }
	//        System.out.println(requestUrl + requestParams);
	//        HttpHeaders headers = new HttpHeaders();
	        String usernamePassword = ":" + bingAppKey;
	//        System.out.println(appKey);
	        String authValue = "Basic "+ new String(Base64.getEncoder().encode(usernamePassword.getBytes()));
	//        System.out.println(authValue);
	//        headers.add("Authorization", authValue);
	//        headers.add("Cache-Control", "no-cache");
	//        HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
	//        ResponseEntity<ImageSearchCollection> responseEntity;
	//        responseEntity = restTemplate.exchange(requestUrl + requestParams, HttpMethod.GET, httpEntity, ImageSearchCollection.class);
	//        ImageSearchCollection searchResults = responseEntity.getBody();
	        URL url = new URL(requestUrl + requestParams);
	        URLConnection connection = url.openConnection();
	        connection.setRequestProperty("Authorization", authValue);
	        connection.setRequestProperty("Cache-Control", "no-cache");
	        
	        File tempFile = null;
	        try (final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
	            String inputLine;
	            StringBuilder response = new StringBuilder();
	            while ((inputLine = in.readLine()) != null) {
	                response.append(inputLine);
	            }
	            JSONObject json = new JSONObject(response.toString());
	            JSONObject d = json.getJSONObject("d");
	            JSONArray results = d.getJSONArray("results");
	            int resultsLength = results.length();
	            for (int i = 0; i < resultsLength; i++) {
	                JSONObject result = results.getJSONObject(i);
	                String downloadUrlString = result.getString("MediaUrl").toString();
	                URL downloadUrl = new URL(downloadUrlString);
	                System.out.println(downloadUrlString);
	                HttpURLConnection imageConnection = (HttpURLConnection) downloadUrl.openConnection();
	                imageConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36");
	                imageConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
	                imageConnection.setRequestProperty("Accept-Encoding", "identity");
	                imageConnection.setRequestProperty("Cache-Control", "no-cache");
	                
	                String redirectUrl = imageConnection.getHeaderField("Location");
	                if (redirectUrl != null){
	                	downloadUrlString = redirectUrl;
	                	imageConnection = (HttpURLConnection) new URL(downloadUrlString).openConnection();
		                imageConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36");
		                imageConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		                imageConnection.setRequestProperty("Accept-Encoding", "identity");
		                imageConnection.setRequestProperty("Cache-Control", "no-cache");
	                }
	                Long expectedLength = imageConnection.getContentLengthLong();
	                System.out.println("Expected Size: " + expectedLength);
	                if (expectedLength < 1000L){
	                	System.out.println("Invalid Content Length.");
	                	continue;
	                }
                
	                
	                tempFile = new File("" + query + i);
	                System.out.print("Downloading...");
	                ReadableByteChannel rbc = null;
	                FileOutputStream fos = null;
	                InputStream is = null;
	                try {
	                is = imageConnection.getInputStream();
	                rbc = Channels.newChannel(is);
	                fos = new FileOutputStream(tempFile.getName());
	                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	                System.out.print("Done!");
	                } catch (Exception e){
	                	System.out.println("Failed to Download.");
	                	if (is != null){
	                		is.close();
	                	} 
	                	if (rbc != null){
	                		rbc.close();
	                	}
	                	if (fos != null){
	                		fos.close();
	                	} 
	                	tempFile.delete();
	                	continue;
	                } finally {
	                	if (is != null){
	                		is.close();
	                	} 
	                	if (rbc != null){
	                		rbc.close();
	                	}
	                	if (fos != null){
	                		fos.close();
	                	} 
	                }
	                
	                
					ByteArrayOutputStream byteArrOutput = new ByteArrayOutputStream();
//		            ByteArrayOutputStream imageByteArrOutput = new ByteArrayOutputStream();
//		            BufferedImage bufferedImage = null;
					Thumbnails.of(tempFile).size(960, 1200).crop(Positions.CENTER).addFilter(new Canvas(960, 1600, Positions.CENTER, true)).outputQuality(0.8).outputFormat("jpg").toOutputStream(byteArrOutput);		           
					//bufferedImage = ImageIO.read (new ByteArrayInputStream(byteArr));
//			            ImageIO.write(bufferedImage, "jpg", imageByteArrOutput);


					byte[] compressedByteArr = byteArrOutput.toByteArray();
					InputStream compressedByteIStream = new ByteArrayInputStream(compressedByteArr);
					
	                hashedName = new String();
	                
	                FileInputStream inputStream = null;
	                try {
	                	inputStream = new FileInputStream(tempFile.getName());
	                	System.out.print(" Hashing...");
	                    MessageDigest digest = MessageDigest.getInstance("MD5");
	             
	                    byte[] bytesBuffer = new byte[1024];
	                    int bytesRead = -1;
	             
	                    while ((bytesRead = inputStream.read(bytesBuffer)) != -1) {
	                        digest.update(bytesBuffer, 0, bytesRead);
	                    }
	             
	                    byte[] hashedBytes = digest.digest();
	    
	                    int lastPeriodIndex = downloadUrlString.lastIndexOf(".");
	                    int lastHashTagIndex = downloadUrlString.lastIndexOf("#");
	                    int lastQuestionIndex = downloadUrlString.lastIndexOf("?");
	                    int lastAmpersandIndex = downloadUrlString.lastIndexOf("&");
	                    String urlTrimmed;
	                    if (lastHashTagIndex > lastPeriodIndex){
	                    	urlTrimmed = downloadUrlString.substring(0, lastHashTagIndex);
	                    } else if (lastQuestionIndex > lastPeriodIndex) {
	                    	urlTrimmed = downloadUrlString.substring(0, lastQuestionIndex);
	                    } else if (lastAmpersandIndex > lastPeriodIndex){
	                    	urlTrimmed = downloadUrlString.substring(0, lastAmpersandIndex);
	                    } else
	                    {
	                    	urlTrimmed = downloadUrlString;
	                    }
	                    
	                    hashedName = convertByteArrayToHexString(hashedBytes) + urlTrimmed.substring(urlTrimmed.lastIndexOf("."));
	                } catch (Exception e){
	                	System.out.println("Failed to Hash.");
	                } finally {
	                	if (inputStream != null){
	                        inputStream.close();
	                	}
	                }
	                
	                
	                
	        		tempFile.delete();
	                
	                if (imageRepo.findByHash("item/" + hashedName).isEmpty()){
		            
		                //Generate Tags
		                //
		                //
		                //
		                //
		                //
	                	
			            ObjectMetadata objectMetaData = new ObjectMetadata();
			            objectMetaData.setContentLength(compressedByteArr.length);
			            objectMetaData.setContentType("image/jpeg");
		                System.out.print(" Uploading to S3...");
		                
		                try{
		                	s3client.putObject(new PutObjectRequest(awsBucketName, hashedName, compressedByteIStream, objectMetaData));
		                }  catch (AmazonServiceException ase) {
		                    System.out.println("Caught an AmazonServiceException, which " +
		                    		"means your request made it " +
		                            "to Amazon S3, but was rejected with an error response" +
		                            " for some reason.");
		                    System.out.println("Error Message:    " + ase.getMessage());
		                    System.out.println("HTTP Status Code: " + ase.getStatusCode());
		                    System.out.println("AWS Error Code:   " + ase.getErrorCode());
		                    System.out.println("Error Type:       " + ase.getErrorType());
		                    System.out.println("Request ID:       " + ase.getRequestId());
		                    return "s3 Upload Failed";
		                } catch (AmazonClientException ace) {
		                    System.out.println("Caught an AmazonClientException, which " +
		                    		"means the client encountered " +
		                            "an internal error while trying to " +
		                            "communicate with S3, " +
		                            "such as not being able to access the network.");
		                    System.out.println("Error Message: " + ace.getMessage());
		                    return "s3 Upload Failed";
		                    
		                }
		                 
	                	System.out.println("Done!");
	                	
//	                	try {
//	            			System.out.print("Generating pre-signed URL...");
//	            			Date expiration = new Date();
//	            			long milliSeconds = expiration.getTime();
//	            			milliSeconds += 1000 * 60 * 60; // Add 1 hour.
//	            			expiration.setTime(milliSeconds);
//
//	            			GeneratePresignedUrlRequest generatePresignedUrlRequest = 
//	            				    new GeneratePresignedUrlRequest(awsBucketName, hashedName);
//	            			generatePresignedUrlRequest.setMethod(com.amazonaws.HttpMethod.GET);
//	            			generatePresignedUrlRequest.setExpiration(expiration);
//
//	            			URL presignedUrl = s3client.generatePresignedUrl(generatePresignedUrlRequest); 
//
//	            			System.out.println(" Pre-Signed URL = " + presignedUrl.toString());
//	            		} catch (AmazonServiceException exception) {
//	            			System.out.println("Caught an AmazonServiceException, " +
//	            					"which means your request made it " +
//	            					"to Amazon S3, but was rejected with an error response " +
//	            			"for some reason.");
//	            			System.out.println("Error Message: " + exception.getMessage());
//	            			System.out.println("HTTP  Code: "    + exception.getStatusCode());
//	            			System.out.println("AWS Error Code:" + exception.getErrorCode());
//	            			System.out.println("Error Type:    " + exception.getErrorType());
//	            			System.out.println("Request ID:    " + exception.getRequestId());
//	            		} catch (AmazonClientException ace) {
//	            			System.out.println("Caught an AmazonClientException, " +
//	            					"which means the client encountered " +
//	            					"an internal error while trying to communicate" +
//	            					" with S3, " +
//	            			"such as not being able to access the network.");
//	            			System.out.println("Error Message: " + ace.getMessage());
//	            		}
	                	
//			            ObjectMetadata tagProcessMetaData = new ObjectMetadata();
//			            tagProcessMetaData.setContentLength(compressedByteArr.length);
//			            tagProcessMetaData.setContentType("image/jpeg");
//
//	                	try {
//		            	System.out.println("Uploading to s3 to generate tags...");
//	            		s3client.putObject(
//	            				   new PutObjectRequest(awsTagProcessBucketName, hashedName, compressedByteIStream, tagProcessMetaData)
//	            				      .withCannedAcl(CannedAccessControlList.PublicRead));
//	            		s3TagProcessUrl = s3client.getResourceUrl(awsTagProcessBucketName, hashedName);
//	            		s3TagProcessUrl = s3TagProcessUrl.replaceFirst("https", "http");
//
//	                	}  catch (AmazonServiceException ase) {
//			                System.out.println("Caught an AmazonServiceException, which " +
//			                		"means your request made it " +
//			                        "to Amazon S3, but was rejected with an error response" +
//			                        " for some reason.");
//			                System.out.println("Error Message:    " + ase.getMessage());
//			                System.out.println("HTTP Status Code: " + ase.getStatusCode());
//			                System.out.println("AWS Error Code:   " + ase.getErrorCode());
//			                System.out.println("Error Type:       " + ase.getErrorType());
//			                System.out.println("Request ID:       " + ase.getRequestId());
//			            } catch (AmazonClientException ace) {
//			                System.out.println("Caught an AmazonClientException, which " +
//			                		"means the client encountered " +
//			                        "an internal error while trying to " +
//			                        "communicate with S3, " +
//			                        "such as not being able to access the network.");
//			                System.out.println("Error Message: " + ace.getMessage());
//			            }
//			            
//			            	
//                		List<String> listOfTags = null;
//			            try{
//		                	System.out.print(" Generating Clarifai Tags...");
//		                	
//
//		                	ClarifaiResp clarifaiResp = null;
//		                	try {
//				                String clarifaiRequestUrl = clarifaiApiUrl + "tag/?url=" + s3TagProcessUrl;
//				                RestTemplate restTemplate = new RestTemplate();
//				                MappingJackson2HttpMessageConverter jsonToPojo = new MappingJackson2HttpMessageConverter();
//				                jsonToPojo.getObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
//				                restTemplate.getMessageConverters().add(jsonToPojo);
//				                HttpHeaders headers = new HttpHeaders();
//				                headers.add("Authorization", "Bearer " + clarifaiAccessToken);
//				                HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
//				                ResponseEntity<ClarifaiResp> responseEntity;
//				                responseEntity = restTemplate.exchange(clarifaiRequestUrl, HttpMethod.GET, httpEntity, ClarifaiResp.class);
//				                clarifaiResp = responseEntity.getBody();
//		                	} catch (Exception e){
//		                		System.out.println("Attempting to obtain clarifai token...");
//		                		String clarifaiTokenRequestUrl = clarifaiApiUrl + "token/?grant_type=client_credentials&client_id="
//		                				+ clarifaiApikey + "&client_secret=" + clarifaiApiSecret;
//				                RestTemplate restTemplate2 = new RestTemplate();
//				                MappingJackson2HttpMessageConverter jsonToPojo2 = new MappingJackson2HttpMessageConverter();
//				                jsonToPojo2.getObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
//				                restTemplate2.getMessageConverters().add(jsonToPojo2);
//				                HttpHeaders headers2 = new HttpHeaders();
//				                HttpEntity<String> httpEntity2 = new HttpEntity<String>(headers2);
//				                ResponseEntity<OAuth2AccessTokenResp> responseEntity2;
//				                responseEntity2 = restTemplate2.exchange(clarifaiTokenRequestUrl, HttpMethod.POST, httpEntity2, OAuth2AccessTokenResp.class);
//				                OAuth2AccessTokenResp oAuth2TokenResp = responseEntity2.getBody();
//				                clarifaiAccessToken = oAuth2TokenResp.getAccessToken();
//				                
//				                String clarifaiRequestUrl = clarifaiApiUrl + "tag/?url=" + s3TagProcessUrl;
//				                RestTemplate restTemplate = new RestTemplate();
//				                MappingJackson2HttpMessageConverter jsonToPojo = new MappingJackson2HttpMessageConverter();
//				                jsonToPojo.getObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
//				                restTemplate.getMessageConverters().add(jsonToPojo);
//				                HttpHeaders headers = new HttpHeaders();
//				                headers.add("Authorization", "Bearer " + clarifaiAccessToken);
//				                HttpEntity<String> httpEntity = new HttpEntity<String>(headers);
//				                ResponseEntity<ClarifaiResp> responseEntity;
//				                responseEntity = restTemplate.exchange(clarifaiRequestUrl, HttpMethod.GET, httpEntity, ClarifaiResp.class);
//				                clarifaiResp = responseEntity.getBody();
//		                	}
//		                	
//			                   
//		                	ClarifaiResult clarifaiResult = clarifaiResp.getResults().get(0);
//		                	ClarifaiTag clarifaiTag = clarifaiResult.getClarifaiFinalResult().getClarifaiTag();
//		                	listOfTags = clarifaiTag.getClasses();
// 	
//			            } catch (Exception e){
//			            	System.out.println("Failed to Generate Clarifai Tags");
//			            	System.out.println(e.getMessage());
//			            }
////		                try{
////		                	System.out.print(" Generating Tags...");
////			                String rekognitionRequestUrl = "http://rekognition.com/func/api?api_key="
////			                + rekognitionApiKey + "&api_secret=" + rekognitionApiSecret + "&jobs=scene_understanding_3&urls="
////			                + downloadUrlString;
////			                RestTemplate restTemplate = new RestTemplate();
////			                MappingJackson2HttpMessageConverter jsonToPojo = new MappingJackson2HttpMessageConverter();
////			                jsonToPojo.getObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
////			                restTemplate.getMessageConverters().add(jsonToPojo);
////			                RekognitionResp rekognitionResp = restTemplate.getForObject(rekognitionRequestUrl, RekognitionResp.class);
////			                RekognitionSceneUnderstanding rekognitionSceneUnderstanding = rekognitionResp.getRekognitionSceneUnderstanding();
////
////			                for (RekognitionMatch match: rekognitionSceneUnderstanding.getRekognitionMatchList()){
////			                	if (match.getScore() >= 0.5){
////			                		listOfTags.add(match.getTag());
////			                	}
////			                }
////		                	System.out.print("Done!");
////			                
////		                } catch (Exception e){
////		                	System.out.print("Failed to Generate Tags.");
////		                }
////		               
//		                System.out.print(" Saving to database...");
//		                String tagsString = new String();
//		                for (String tag: listOfTags){
//		                	List<Tag> currentTags = tagRepo.findByName(tag);
//		                	if (currentTags.isEmpty()){
//		                		Tag newTag = new Tag(tag, 1);
//			                	tagRepo.save(newTag);
//		                	} else if (currentTags.size() == 1){
//		                		Tag existingTag = currentTags.get(0);
//		                		existingTag.setCount(existingTag.getCount() + 1);
//			                	tagRepo.save(existingTag);
//		                	} else {
//		                		System.out.println(" Fatal Error! Duplicate Tags!");
//		                		throw new Exception();
//		                	}
//
//		                	tagsString = tagsString + tag + ",";
//		                }
//		                
//		                
//		                if (!tagsString.isEmpty()){
//		                	tagsString = tagsString.substring(0, tagsString.lastIndexOf(","));
//	                	}
	                
		                Image image = new Image("item/" + hashedName, query, null, null);
		                try {
		                	imageRepo.save(image);
			                System.out.println("Done!");
		                } catch (Exception e){
		                	System.out.println("Failed to save to database.");
		                }

	                } else {
	                	System.out.println(" Image already exists in database");
	                }
	                
	            }
	            
	        } finally {
	        	if (tempFile != null){
	        		tempFile.delete();
	        	} if (s3TagProcessUrl != null){
					s3client.deleteObject(awsTagProcessBucketName, hashedName);
	        	}
	            
	        }
        
		}
		
        return "Finished!";
		
	}
	

    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }
}
