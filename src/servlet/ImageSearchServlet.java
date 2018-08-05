package servlet;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.invoke.MethodHandles;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.ObjectMapper;

import dto.ImageDataRequest;
import util.auth.AmazonConnector;

/**
 * Servlet implementation class ImageSearchServlet
 */
@WebServlet("/image")
public class ImageSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(MethodHandles.lookup().lookupClass());
	private static String SEARCH_TARGET_PATH = "xxxx";
	private static String SAVE_TARGET_PATH = "xxxx";
	private static String SEARCH_URL = "http://www.google.co.jp/searchbyimage?image_url=%s";

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			logger.info("receive POST request");

			// 撮影した画像を取得
			String json = request.getReader().lines().collect(Collectors.joining("\r\n"));
			ObjectMapper mapper = new ObjectMapper();
			ImageDataRequest imageRequest = mapper.readValue(json, ImageDataRequest.class);

			// 撮影した画像をjpgで保存
			byte[] imageBinary = Base64.decodeBase64(imageRequest.imageData);
			ByteArrayInputStream inputImage = new ByteArrayInputStream(imageBinary);
			BufferedImage image = ImageIO.read(inputImage);
			FileOutputStream output = new FileOutputStream(SAVE_TARGET_PATH);
			ImageIO.write(image, "jpg", output);

			// 画像を公開
			String searchTargetPath = AmazonConnector.upload();

			// 撮影した画像でGoogle画像検索
			Document document = Jsoup.connect(String.format(SEARCH_URL, String.format(SEARCH_TARGET_PATH, searchTargetPath))).get();
			Elements elements = document.select("#topstuff > div > div > a");
			String [] keywords = elements.text().split(" ");
			String result = "...";
			if (keywords.length != 0) {
				result = keywords[0];
			}
			// 検索結果を返却
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			String responseJson = "{\"responseMessage\" : \"" + result + "\"}";
			out.println(responseJson);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
