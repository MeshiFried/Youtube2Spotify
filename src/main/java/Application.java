
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;

public class Application {
	
	private static SpotifyClient spotifyClient;
	private static SpotifyManager spotifyManager;
	private static Scanner scanner = new Scanner(System.in);
	
    public static void main(String[] args) throws GeneralSecurityException, IOException, GoogleJsonResponseException {
    		
    		getAccessToSpotify();
    	
    		ArrayList<String> titles;
			try {
				titles = getLikedVideosFromYoutube();
	    		createSpotifyPlaylist(titles);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		

    }
    
    public static void getAccessToSpotify() {
    	System.out.println("Please enter your Spotify Client ID:");
    	String clientId = scanner.nextLine();
    	System.out.println("Please enter your Spotify Client Secret:");
    	String clientSecret = scanner.nextLine();
    	
    	spotifyClient = new SpotifyClient(clientId, clientSecret);
    	spotifyClient.authorizationCodeUri_Sync();
    	String authorizationCode = scanner.nextLine();
        spotifyClient.setAuthorizationCodeRequest(authorizationCode);
        String accessToken = spotifyClient.getAccessToken();
        
        System.out.println("Please enter your Spotify User ID:");
    	String userId = scanner.nextLine();
    	
    	spotifyManager = new SpotifyManager(accessToken, userId);
        

    	
    }
    
    public static ArrayList<String> getLikedVideosFromYoutube() throws Exception, IOException {
    	System.out.println("A browser will open your google account, please give permission for this app");
    	Thread.sleep(3000);
    	YouTube youtubeService = YoutubeClient.getService();
		// Define and execute the API request
		YouTube.Videos.List request = youtubeService.videos()
				.list("snippet");
		VideoListResponse response = request.setMaxResults(100L).setMyRating("like").execute();
		ArrayList<String> titles = new ArrayList<String>();
		while(response.getNextPageToken() != null) {
			titles.addAll(response.getItems().stream().map(t -> t.getSnippet().getTitle()).collect(Collectors.toList())); 
			response = request.setPageToken(response.getNextPageToken()).setMaxResults(100L).setMyRating("like").execute();
		}
		
		return titles;
    }
    
    public static void createSpotifyPlaylist(ArrayList<String> titles) {
    	System.out.println("Please enter playlist name:");
    	String playlist = scanner.nextLine();
		String playlistId = spotifyManager.createPlaylist_Sync(playlist);

		System.out.println(titles.toString());
		for(String title : titles) {
			System.out.println(title);
			ArrayList<String> track = new ArrayList<String>();
			track.add(title);
			String[] uris = spotifyManager.getTrackUris(track);
			if(uris.length > 0) {
				spotifyManager.addTracks(playlistId, uris);
			}
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }
}