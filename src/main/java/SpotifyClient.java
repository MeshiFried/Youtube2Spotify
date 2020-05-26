
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.hc.core5.http.ParseException;


import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.SpotifyHttpManager;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

public class SpotifyClient {
  private String clientId;
  private String clientSecret;
  private static final URI redirectUri = SpotifyHttpManager.makeUri("https://example.com/spotify-redirect");
  private SpotifyApi spotifyApi;
  private AuthorizationCodeUriRequest authorizationCodeUriRequest;
  private AuthorizationCodeRequest authorizationCodeRequest;
  
  public SpotifyClient(String clientId, String clientSecret) {
	  this.spotifyApi = new SpotifyApi.Builder()
			    .setClientId(clientId)
			    .setClientSecret(clientSecret)
			    .setRedirectUri(redirectUri)
			    .build();
	  this.clientId = clientId;
	  this.clientSecret = clientSecret;
	  
	 this.authorizationCodeUriRequest= spotifyApi.authorizationCodeUri()
//	          .state("x4xkmn9pu3j6ukrs8n")
	          .scope("playlist-modify-public")
	          .show_dialog(true)
	    .build();
	  
  }
 

  public void authorizationCodeUri_Sync() {
    final URI uri = authorizationCodeUriRequest.execute();

    System.out.println("Please Enter the following URI to a browser and than enter the Code from the URL: " + uri.toString());    
  }
  
  public void setAuthorizationCodeRequest(String code) {
	  authorizationCodeRequest = spotifyApi.authorizationCode(code)
			    .build();
  }


  
  public String getAccessToken() {
	try {
		AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();
		return authorizationCodeCredentials.getAccessToken();
	} catch (ParseException | SpotifyWebApiException | IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
  }
  
}