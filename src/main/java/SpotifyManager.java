
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.hc.core5.http.ParseException;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.special.SnapshotResult;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import com.wrapper.spotify.requests.data.playlists.CreatePlaylistRequest;
import com.wrapper.spotify.requests.data.search.simplified.SearchTracksRequest;

public class SpotifyManager {
  private String accessToken;
  private String userId;
  private SpotifyApi spotifyApi;
  
  public SpotifyManager(String accessToken, String userId) {
	  this.accessToken = accessToken;
	  this.userId = userId;
	  this.spotifyApi = new SpotifyApi.Builder()
			    .setAccessToken(accessToken)
			    .build();
  }

   
  public String createPlaylist_Sync(String name) {
    try {
	 CreatePlaylistRequest createPlaylistRequest = spotifyApi.createPlaylist(userId, name)
//              .collaborative(false)
//              .public_(false)
//              .description("Amazing music.")
    .build();

      final Playlist playlist = createPlaylistRequest.execute();
      if(playlist != null) {
    	  return playlist.getId();
      }
      
      System.out.println("Name: " + playlist.getName());
    } catch (IOException | SpotifyWebApiException | ParseException e) {
      System.out.println("Error: " + e.getMessage());
    }
    
    return null;
  }
  
  public String[] getTrackUris(ArrayList<String> tracks) {
	  ArrayList<String> uris = new ArrayList<String>();
	  for(String track: tracks) {
		  final SearchTracksRequest searchTracksRequest = spotifyApi.searchTracks(track)
				  //	          .market(CountryCode.SE)
				  //	          .limit(10)
				  //	          .offset(0)
				  //	          .includeExternal("audio")
				  .build();
		  try {
			  final Paging<Track> trackPaging = searchTracksRequest.execute();
			  if(trackPaging.getItems().length > 0) {
				  uris.add(trackPaging.getItems()[0].getUri());
			  }
		  } catch (ParseException | SpotifyWebApiException | IOException e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
		  }
	  }

	  return uris.toArray(new String[uris.size()]);
  }

  public void addTracks(String playlistId, String[] uris) {
	 AddItemsToPlaylistRequest addItemsToPlaylistRequest = spotifyApi
			    .addItemsToPlaylist(playlistId, uris)
//			          .position(0)
			    .build();
	 try {
		final SnapshotResult snapshotResult = addItemsToPlaylistRequest.execute();
	} catch (ParseException | SpotifyWebApiException | IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
}