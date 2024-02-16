package com.hsm.macs.campusguide;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.hsm.macs.campusguide.courseinfo.CourseInfo;
import com.hsm.macs.campusguide.walkeitalkei.WalkeiTalkeiMainActivity;

public class HomeActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_CODE_PERMISSION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageButton mapCard, walkeiTalkeiCard, subjectListCard;

        mapCard = findViewById(R.id.mapCard);

        mapCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndRequestPermission(LOCATION_PERMISSION_REQUEST_CODE);
            }
        });

        walkeiTalkeiCard = findViewById(R.id.walkeiTalkei);
        walkeiTalkeiCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // checkAndRequestPermission(REQUEST_CODE_PERMISSION);
                showHostDialog();
            }
        });

        subjectListCard = findViewById(R.id.courseInfo);
        subjectListCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CourseInfo.class);
                startActivity(intent);
            }
        });
    }

    private void checkAndRequestPermission(int PERMISSION_CODE) {
        if(PERMISSION_CODE == LOCATION_PERMISSION_REQUEST_CODE) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                openMap();
            } else {
                // Permission is not granted
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Show an explanation to the user, e.g., in a dialog
                    showPermissionExplanationDialog("Location");
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                    Toast.makeText(this, "Allow Location Permission", Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.NEARBY_WIFI_DEVICES)
                    == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted

            } else {
                // Permission is not granted
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.NEARBY_WIFI_DEVICES)) {
                    // Show an explanation to the user, e.g., in a dialog
                    showPermissionExplanationDialog("Location & Near by wifi devices");
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSION);
                }
            }
        }
    }

    private void showPermissionExplanationDialog(String permission) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Required")
                .setMessage("This app requires " + permission + " permission to function properly.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Request the permission
                        ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void openMap() {
        Intent intentMap = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intentMap);
    }
    private void showHostDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you host?");

        // Inflate a custom view for the dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_host, null);
        builder.setView(dialogView);

        final RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);
        final EditText editTextHostId = dialogView.findViewById(R.id.editTextHostId);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButtonNo) {
                    editTextHostId.setVisibility(View.VISIBLE);
                    editTextHostId.setEnabled(true);
                } else {
                    editTextHostId.setVisibility(View.GONE);
                    editTextHostId.setText(""); // Clear the text when "No" is not selected
                }
            }
        });

        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                if (selectedRadioButtonId != -1) {
                    RadioButton selectedRadioButton = dialogView.findViewById(selectedRadioButtonId);
                    String answer = selectedRadioButton.getText().toString();
                    boolean isHost = answer.equals("Yes");
                    String hostId = null;

                    if (!isHost) {
                        // If "No" is selected, retrieve the host ID from the EditText
                        EditText editTextHostId = dialogView.findViewById(R.id.editTextHostId);
                        hostId = editTextHostId.getText().toString();

                        // Validate the host ID
                        if (isValidHostId(hostId)) {
                            // Start an intent with the selected answer and host ID
                            Intent intent = new Intent(HomeActivity.this, WalkeiTalkeiMainActivity.class);
                            intent.putExtra("is_Host", false);
                            intent.putExtra("host_Id", hostId);
                            startActivity(intent);
                        } else {
                            // Host ID is not valid, show a message or handle accordingly
                            Toast.makeText(HomeActivity.this, "Invalid Host ID!!! Host id  should be 5 digit number", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Start an intent when "Yes" is selected without checking host ID
                        Intent intent = new Intent(HomeActivity.this, WalkeiTalkeiMainActivity.class);
                        intent.putExtra("host_Id", (String) null);
                        intent.putExtra("is_Host", true);
                        startActivity(intent);
                    }

                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Function to validate the host ID
    private boolean isValidHostId(String hostId) {
        if (hostId.isEmpty() || hostId.length() != 5) {
            return false; // Not a valid 5-digit integer
        }
        try {
            int id = Integer.parseInt(hostId);
            return true; // Valid 5-digit integer
        } catch (NumberFormatException e) {
            return false; // Not a valid integer
        }
    }


//    private boolean arePermissionsGranted() {
//        return false;
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                openMap();
            } else {
                // Permission is denied
                showPermissionDeniedDialog();
            }
        }else if (requestCode == REQUEST_CODE_PERMISSION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted

            } else {
                // Permission is denied
                showPermissionDeniedDialog();
            }
        }
    }

    private void showPermissionDeniedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Denied")
                .setMessage("You have denied location permission. Some features of the app may not work.")
                .setPositiveButton("OK", null)
                .show();
    }
}
