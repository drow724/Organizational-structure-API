package com.org.api.service;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;
import software.amazon.awssdk.services.lambda.model.LambdaException;

@Service
public class OrgMappingService {

	private final String functionName = "OrganizationalStructureBatch";

	@Value("${cloud.aws.credentials.accessKey}")
	private String accessKey;

	@Value("${cloud.aws.credentials.secretKey}")
	private String secretKey;

	@Async
	public void mapping(byte[] document) {
		Region region = Region.AP_NORTHEAST_2;
		LambdaClient awsLambda = LambdaClient.builder().region(region)
				.credentialsProvider(() -> AwsBasicCredentials.create(accessKey, secretKey)).build();

		InvokeResponse res = null;

		try {
			// Need a SdkBytes instance for the payload.
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("file", document);
			
			SdkBytes payload = SdkBytes.fromUtf8String(jsonObj.toString());

			// Setup an InvokeRequest.
			InvokeRequest request = InvokeRequest.builder().functionName(functionName).payload(payload).build();

			res = awsLambda.invoke(request);

		} catch (LambdaException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			awsLambda.close();
		}
		
	}
}
