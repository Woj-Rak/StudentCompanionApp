package com.example.wojtek.studentcompanion.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;

import com.example.wojtek.studentcompanion.Activities.MainActivity;
import com.example.wojtek.studentcompanion.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Fragment For the Audio Section
 */
public class AudioFragment extends Fragment {

    private static final String TAG = "AUDIO FRAGMENT";

    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer ;



    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public AudioFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AudioFragment newInstance(int sectionNumber) {
        AudioFragment fragment = new AudioFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    //Method used for displaying the menu allowing the user name their recording
    public Dialog newRecordingMenu(){
        final EditText taskEditText = new EditText(getActivity());
        taskEditText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Name the audio file")
                .setView(taskEditText)
                .setPositiveButton(R.string.recordingConfirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //The user input is used to set the title of the Marker
                        String userInput = taskEditText.getText().toString();

                        //The file path is set to the right directory wiht the user input as the name of the file.
                        AudioSavePathInDevice =
                                Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                        "StudentCompanion" + "/" + userInput;

                        //Here the actual recording takes place.
                        MediaRecorderReady();

                        try {
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                        } catch (IllegalStateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        //Relevant buttons are hidden and shown to the user during the recording.
                        final FloatingActionButton recordFab = (FloatingActionButton) getView().findViewById(R.id.recordfab);
                        final FloatingActionButton stopRecFab = (FloatingActionButton) getView().findViewById(R.id.stoprecordfab);
                        recordFab.hide();
                        stopRecFab.show();

                        //Toast message to confirm that the recording has started.
                        Toast.makeText(getActivity(), "Recording started",
                                Toast.LENGTH_LONG).show();

                    }
                })
                .setNegativeButton(R.string.todoCancelBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Does nothing, just closes the alert dialog
                    }
                });
        return builder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_audio, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.audioList);

        final FloatingActionButton recordFab = (FloatingActionButton) rootView.findViewById(R.id.recordfab);
        final FloatingActionButton stopRecFab = (FloatingActionButton) rootView.findViewById(R.id.stoprecordfab);
        final FloatingActionButton stopPlayFab = (FloatingActionButton) rootView.findViewById(R.id.stopplayfab);

        //Checks if the directory for Audio Recordings exists, if not it is created.
        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                "StudentCompanion" + "/");
        if(f.isDirectory()) {
            //No Code required here
        }else{

            // create a File object for the parent directory
            File audioDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/StudentCompanion");
            // have the object build the directory structure, if needed.
            audioDirectory.mkdirs();
        }

        //Collects all the files in the directory and lists them in the listview
        String[] files = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "StudentCompanion" + "/").list();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, files);
        listView.setAdapter(adapter);

        //hide and show will be used throughout the code to ensure that only relevant buttons are
        //displayed to the user.
        stopRecFab.hide();
        stopPlayFab.hide();

        //Functionality for playing the recordings by clicking on them.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Item CLICKED " + position);

                //Only the stop button is available to the user at the time of listening
                stopRecFab.hide();
                recordFab.hide();
                stopPlayFab.show();

                //The correct recording is found
                String listItem = (adapter.getItem(position));
                AudioSavePathInDevice =
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                "StudentCompanion" + "/" + listItem;

                //Here the media player is set up with the right file path.
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(AudioSavePathInDevice);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //The recording starts playing at a toast message is displayed as a confirmation
                mediaPlayer.start();
                Toast.makeText(getActivity(), "Recording Playing",
                        Toast.LENGTH_LONG).show();

            }
        });

        //Functionality for deleting recordings by holding down on them
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                Log.d(TAG, "Item HELD " + position);
                //AlertDialog is used to ask the user for confirmation of their decision
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Would you like to delete this recording?")
                        .setPositiveButton(R.string.recordingDelete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //listItem is set to the selected item
                                String listItem = (adapter.getItem(position));

                                //The file path is set according to the item the user selected
                                AudioSavePathInDevice =
                                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                                "StudentCompanion" + "/" + listItem;

                                //Here the file is deleted
                                File deleteFile = new File (AudioSavePathInDevice);
                                deleteFile.delete();

                                //The list is updated to reflect the change
                                adapter.notifyDataSetChanged();
                                updateList();
                            }
                        })
                        .setNegativeButton(R.string.todoCancelBtn, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Does nothing, just closes the alert dialog
                            }
                        });
                builder.show();
                return true;
            }
        });

        recordFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check for permissions and if they were granted we run the recording method.
                if(checkPermission()) {
                    newRecordingMenu().show();
                } else {
                    requestPermission();
                }
            }
        });

        //Functionality to stop the recording.
        stopRecFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                stopRecFab.hide();
                recordFab.show();
                stopPlayFab.hide();

                //Toast message displayed to confirm that the recording has been stopped
                Toast.makeText(getActivity(), "Recording Completed",
                        Toast.LENGTH_LONG).show();
                //Update the listview to reflect the changes
                adapter.notifyDataSetChanged();
                updateList();
            }
        });

        //Stops the playing of a recording
        stopPlayFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecFab.hide();
                recordFab.show();
                stopPlayFab.hide();

                if(mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    MediaRecorderReady();
                }
            }
        });
        return rootView;
    }

    //Method used to ready the recorder before a recording takes place
    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    //Permission checking methods to record audio and save to storage
    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(getActivity(), "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(),"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    //Method used to update the listview after a recording has been added or deleted.
    public void updateList(){
        ListView listView = (ListView) getView().findViewById(R.id.audioList);
        String[] files = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "StudentCompanion" + "/").list();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, files);
        listView.setAdapter(adapter);
    }

}
