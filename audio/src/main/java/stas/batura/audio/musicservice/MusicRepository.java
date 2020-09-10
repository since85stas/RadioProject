package stas.batura.audio.musicservice;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import stas.batura.musicproject.repository.room.Controls;
import stas.batura.musicproject.repository.room.TrackKot;
import stas.batura.musicproject.repository.room.TracksDao;
import stas.batura.musicproject.ui.playlist.AlbumsDataInfo;

import static stas.batura.musicproject.repository.room.ControlsKt.REPEAT_ONE;
import static stas.batura.musicproject.repository.room.ControlsKt.SHUFFLE_ON;

//https://simpleguics2pygame.readthedocs.io/en/latest/_static/links/snd_links.html
public final class MusicRepository {

    private TracksDao repository;

    public MutableLiveData<List<Track>> tracks = new MutableLiveData<>();

    private Controls controls;

    @VisibleForTesting
    public int maxIndex = 20;

    private int currentItemIndex = 0;

    List<TrackKot> tracksDb;

    public MusicRepository(TracksDao repository ) {
        this.repository = repository;

        getDbTracks();
        System.out.println("end repos creat");
    }

    private int getInitIndex(List<Track> trackKotList) {
        int count = 0;
        int index = 0;
        for (Track tr: trackKotList
             ) {
            if (tr.isPlaying) {
                index = count;
            }
            count++;
        }
        return index;
    }

    public void getDbTracks() {
        tracksDb = repository.getAllTracksFromMainPlaylist();
        if (tracksDb != null) {
            updateTracksLive(tracksDb);
        }
    }

    public void updateContols(Controls controls) {
        this.controls = controls;
    }

    void updateTracksLive (List<TrackKot> trackKotList) {

        List<Track> tacksRep = new ArrayList<>();
        for (int i = 0; i < trackKotList.size(); i++) {
            Track track = new Track(trackKotList.get(i));
            tacksRep.add(track );
        }

        // упорядочиваем список песен по альбомам
        AlbumsDataInfo data =new  AlbumsDataInfo(tacksRep);
        tracks.postValue(data.getTracksInAlbumsOrder());

        //
        currentItemIndex = getInitIndex(data.getTracksInAlbumsOrder());
//        tracks.postValue(tacksRep);
        maxIndex = tacksRep.size()-1;
    }

    Track getNext() {
        if (controls.getPlayStatus() == SHUFFLE_ON) {
            currentItemIndex = getRandomNum();
        } else if (controls.getPlayStatus() == REPEAT_ONE) {
            Log.d("music service", "getNext: one");
        } else {
            if (currentItemIndex == maxIndex)
                currentItemIndex = 0;
            else
                currentItemIndex++;
        }
        return getCurrent();
    }

    Track getPrevious() {
        if (controls.getPlayStatus() == SHUFFLE_ON) {
            currentItemIndex = getRandomNum();
        } else {
            if (currentItemIndex == maxIndex)
                currentItemIndex = 0;
            else
                currentItemIndex--;
        }
        return getCurrent();
    }

    Track getTrackByIndex(int index) {
        currentItemIndex = index;
        return getCurrent();
    }

    @VisibleForTesting
    public int getRandomNum() {
        Random random = new Random();
        return random.nextInt(maxIndex);
    }

    Track getTrackByUri(Uri uri) {
        if (uri != null) {
            currentItemIndex = getIndexByUri(uri);
            return getCurrent();
        }
        return tracks.getValue().get(0);
    }

    public void setPlayByUri(Uri uri) {
        currentItemIndex = getIndexByUri(uri);
    }

    Track getCurrent() {
        setIsPlaying();
        return tracks.getValue().get(currentItemIndex);
    }

    // получаем номер по uri
    private int getIndexByUri (Uri uri) {
        for (int i = 0; i < tracks.getValue().size(); i++) {
            if (tracks.getValue().get(i).uri.equals(uri)) {
                return i;
            }
        }
        return 0;
    }

    private void setIsPlaying() {
        Log.d("musicReppos", "setIsPlaying: ");
        if (tracks.getValue() != null && tracks.getValue().size() > 0) {
            for (Track tr : tracks.getValue()) {
                tr.isPlaying = false;
            }
        }
        repository.setAllTrackIsNOTPlaying();
        repository.setTrackIsPlaying(tracks.getValue().get(currentItemIndex).trackId);
        getDbTracks();
//        tracks.getValue().get(currentItemIndex).isPlaying = true;
//        tracks.setValue(new ArrayList<>(tracks.getValue()));
    }

    public static class Track {
        public int trackId;
        public String title;
        public String artist;
        public String album;
        private Uri bitmapUri;
        public Uri uri;
        public Long duration; // in ms
        public boolean isPlaying;
        public String bitrate;
        public int year;

        Track(int id, String title, String artist,String album, Uri bitmapResId, Uri uri, Long duration, boolean is,
              String bitrate, int year) {
            this.trackId = id;
            this.title = title;
            this.artist = artist;
            this.album = album;
            this.bitmapUri = bitmapResId;
            this.uri = uri;
            this.duration = duration;
            this.isPlaying = is;
            this.bitrate = bitrate;
            this.year    = year;
        }

        Track(TrackKot trackKot) {
            this.trackId = trackKot.get_ID();
            this.title = trackKot.getTitle();
            this.artist = trackKot.getArtist();
            this.album = trackKot.getAlbum();
            this.bitmapUri = trackKot.getBitmapUri();
            this.uri = trackKot.getUri();
            this.duration = trackKot.getDuration();
            this.isPlaying = trackKot.isPlaying();
            this.bitrate = trackKot.getBitrate();
            this.year    = trackKot.getYear();
        }

        String getTitle() {
            return title;
        }

        String getArtist() {
            return artist;
        }

        Uri getBitmapUri() {
            return bitmapUri;
        }

        Uri getUri() {
            return uri;
        }

        Long getDuration() {
            return duration;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Track track = (Track) o;
            return trackId == track.trackId &&
                    bitmapUri == track.bitmapUri &&
                    duration.equals(track.duration) &&
                    title.equals(track.title) &&
                    artist.equals(track.artist) &&
                    uri.equals(track.uri) &&
                    isPlaying == track.isPlaying;
        }

        @Override
        public int hashCode() {
            return Objects.hash(trackId, title, artist, bitmapUri, uri, duration, isPlaying);
        }
    }
}
