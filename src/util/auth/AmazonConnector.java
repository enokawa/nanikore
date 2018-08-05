package util.auth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class AmazonConnector {

    /* AWS情報 */
    static final String S3_ACCESS_KEY           = "xxxx";
    static final String S3_SECRET_KEY           = "xxxx";
    static final String S3_SERVICE_END_POINT    = "xxxx";
    static final String S3_REGION               = "xxxx";
    static final String S3_BUCKET_NAME          = "xxxx";

    private static AmazonS3 auth(){
        System.out.println("auth start");

        // AWSの認証情報
        AWSCredentials credentials = new BasicAWSCredentials(S3_ACCESS_KEY, S3_SECRET_KEY);

        // クライアント設定
        ClientConfiguration clientConfig = new ClientConfiguration();

        // S3アクセスクライアントの生成
        AmazonS3 client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withClientConfiguration(clientConfig).withRegion(S3_REGION).build();

        System.out.println("auth end");
        return client;
    }

    // ｱｯﾌﾟﾛｰﾄﾞ処理
    public static String upload() throws IOException {
        System.out.println("upload start");

        // 認証処理
        AmazonS3 client = auth();

        File file = new File("xxxx");
        Path tmpPath = Files.createTempFile(Paths.get("xxxx"), "prefix", ".jpg");
        String searchFileName = tmpPath.toFile().getName();

        try(FileInputStream fis = new FileInputStream(file)) {
	        ObjectMetadata metadata = new ObjectMetadata();
	        metadata.setContentType("image/jpeg");
	        metadata.setContentLength(file.length());

	        PutObjectRequest putRequest = new PutObjectRequest(S3_BUCKET_NAME, searchFileName, file);
	        putRequest.setMetadata(metadata);

	        // 権限の設定
	        putRequest.setCannedAcl(CannedAccessControlList.PublicRead);

	        // アップロード
	        client.putObject(putRequest);

	        System.out.println("upload end");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return searchFileName;
    }
}
