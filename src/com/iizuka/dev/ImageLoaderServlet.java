package com.iizuka.dev;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.media.mediarss.MediaContent;
import com.google.gdata.data.photos.AlbumEntry;
import com.google.gdata.data.photos.AlbumFeed;
import com.google.gdata.data.photos.PhotoEntry;
import com.google.gdata.data.photos.UserFeed;
import com.google.gdata.util.ServiceException;


public class ImageLoaderServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		resp.setContentType("text/plain;charset=UTF-8");
		try {
//			downloadImage(resp);
			printPhotoList(resp);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
	private List<String> downloadImage(HttpServletResponse resp) throws IOException, ServiceException {
		String username = "google-email";
		String password = "google-password";
		
		PicasawebService myService = new PicasawebService(ImageLoaderServlet.class.getName());
		myService.setUserCredentials(username, password);
		myService.setConnectTimeout(30000);
		myService.setReadTimeout(30000);
		
		URL feedUrl = new URL("https://picasaweb.google.com/data/feed/api/user/" + username + "?kind=album");
		UserFeed myUserFeed = myService.getFeed(feedUrl, UserFeed.class);
		List<String> arrIds = new ArrayList<String>();
		for (AlbumEntry myAlbum : myUserFeed.getAlbumEntries()) {
			resp.getWriter().println(myAlbum.getTitle().getPlainText());
			resp.getWriter().println(myAlbum.getId());
			arrIds.add(myAlbum.getId());
		}
		return arrIds;
	}
	
	private void printPhotoList(HttpServletResponse resp) throws IOException, ServiceException {
		String username = "google-email";
		String password = "google-password";
		
		PicasawebService myService = new PicasawebService(ImageLoaderServlet.class.getName());
		myService.setUserCredentials(username, password);
		myService.setConnectTimeout(300000);
		myService.setReadTimeout(300000);
		
		URL feedUrl = new URL("http://picasaweb.google.com/data/feed/api/user/115133622566092881970/albumid/6054985960428655489");
		AlbumFeed feed = myService.getFeed(feedUrl, AlbumFeed.class);
		for (PhotoEntry photo : feed.getPhotoEntries()) {
			List<MediaContent> l = photo.getMediaContents();
//			resp.getWriter().println(photo.getTitle().getPlainText());
//			resp.getWriter().println(photo.getHtmlLink().getHref());
//			resp.getWriter().println(photo.getId());
//			resp.getWriter().println(l.get(0).getUrl().toString());
			
			URL url = new URL(l.get(0).getUrl().toString());
			URLConnection urlConnection = url.openConnection();
			urlConnection.connect();
			
			byte[] image = new byte[(int)urlConnection.getContentLength()];
			InputStream is = urlConnection.getInputStream();
			int offset = 0;
			do {
				int read = is.read(image, offset, image.length - offset);
				offset += read;
			} while (0 < is.available());
			resp.setContentType("image/jpeg");
			resp.setContentLength(image.length);
			resp.getOutputStream().write(image);
			break;
		}
	}
}
