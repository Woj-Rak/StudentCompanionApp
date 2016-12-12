package com.example.wojtek.studentcompanion.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

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
                .setPositiveButton(R.string.todoAcceptBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //The user input is used to set the title of the Marker
                        String userInput = taskEditText.getText().toString();

                        AudioSavePathInDevice =
                                Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                        "StudentCompanion" + "/" + userInput;



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

                        final FloatingActionButton recordFab = (FloatingActionButton) getView().findViewById(R.id.recordfab);
                        final FloatingActionButton stopRecFab = (FloatingActionButton) getView().findViewById(R.id.stoprecordfab);
                        recordFab.hide();
                        stopRecFab.show();

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
        //final FloatingActionButton playFab = (FloatingActionButton) rootView.findViewById(R.id.playfab);
        final FloatingActionButton stopPlayFab = (FloatingActionButton) rootView.findViewById(R.id.stopplayfab);

        //Checks if the directory for Audio Recordings exists, if not it is created.
        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                "StudentCompanion" + "/");
        if(f.isDirectory()) {
            //Write code for the folder exist condition
        }else{

            // create a File object for the parent directory
            File audioDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/StudentCompanion");
            // have the object build the directory structure, if needed.
            audioDirectory.mkdirs();
        }

        String[] files = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "StudentCompanion" + "/").list();
        ArrayAdapter<String> a = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, files);
        listView.setAdapter(a);


        //hide and show will be used throughout the code to ensure that only relevant buttons are
        //displayed to the user.
        stopRecFab.hide();
        //playFab.hide();
        stopPlayFab.hide();

        recordFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkPermission()) {
                    newRecordingMenu().show();
                    /*
                    AudioSavePathInDevice =
                            Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                    "StudentCompanion" + "/" + "AudioRecording.3gp";



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

                    recordFab.hide();
                    stopRecFab.show();

                    Toast.makeText(getActivity(), "Recording started",
                            Toast.LENGTH_LONG).show();
                    */
                } else {
                    requestPermission();
                }
            }
        });

        stopRecFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                stopRecFab.hide();
                //playFab.show();
                recordFab.show();
                stopPlayFab.hide();

                Toast.makeText(getActivity(), "Recording Completed",
                        Toast.LENGTH_LONG).show();

                updateList();
            }
        });

        /*playFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {

                stopRecFab.hide();
                recordFab.hide();
                stopPlayFab.show();

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(AudioSavePathInDevice);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
                Toast.makeText(getActivity(), "Recording Playing",
                        Toast.LENGTH_LONG).show();
            }
        });
        */

        stopPlayFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecFab.hide();
                recordFab.show();
                stopPlayFab.hide();
                //playFab.show();

                if(mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    MediaRecorderReady();
                }
            }
        });

        return rootView;
    }

    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
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

    public void updateList(){
        ListView listView = (ListView) getView().findViewById(R.id.audioList);
        String[] files = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "StudentCompanion" + "/").list();
        ArrayAdapter<String> a = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, files);
        listView.setAdapter(a);
    }
}
