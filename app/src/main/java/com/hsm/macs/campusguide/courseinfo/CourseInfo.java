package com.hsm.macs.campusguide.courseinfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;





import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hsm.macs.campusguide.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CourseInfo extends Activity {

    private Spinner spinnerCourse, spinnerSemester;
    private RecyclerView recyclerViewSubjects;
    private List<String> coursesList;
    private List<String> semestersList;
    private List<String> subjectsList;
    private ArrayAdapter<String> courseAdapter;
    private ArrayAdapter<String> semesterAdapter;
    private SubjectAdapter subjectAdapter;
    private JSONObject jsonData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_info);

        // Initialize views
        spinnerCourse = findViewById(R.id.spinnerCourse);
        spinnerSemester = findViewById(R.id.spinnerSemester);
        recyclerViewSubjects = findViewById(R.id.recyclerViewSubjects);

        // Initialize lists
        coursesList = new ArrayList<>();
        semestersList = new ArrayList<>();
        subjectsList = new ArrayList<>();

        // Load JSON data
        loadJSONData();

        // Initialize adapters
        courseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, coursesList);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourse.setAdapter(courseAdapter);

        semesterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, semestersList);
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemester.setAdapter(semesterAdapter);

        recyclerViewSubjects.setLayoutManager(new LinearLayoutManager(this));
        subjectAdapter = new SubjectAdapter(this, subjectsList);
        recyclerViewSubjects.setAdapter(subjectAdapter);

        // Spinner listeners
        spinnerCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateSemesterSpinner(position);
                updateSubjectList(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerSemester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateSubjectList(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadJSONData() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.course);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String json = new String(buffer, "UTF-8");
            jsonData = new JSONObject(json);
            JSONArray coursesArray = jsonData.getJSONArray("courses");

            for (int i = 0; i < coursesArray.length(); i++) {
                JSONObject courseObject = coursesArray.getJSONObject(i);
                String courseName = courseObject.getString("course_name");
                coursesList.add(courseName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error json", Toast.LENGTH_SHORT).show();
        }
    }



    private void updateSemesterSpinner(int coursePosition) {
        semestersList.clear();

        try {
            JSONArray semestersArray = jsonData.getJSONArray("courses").getJSONObject(coursePosition).getJSONArray("semesters");

            for (int i = 0; i < semestersArray.length(); i++) {
                JSONObject semesterObject = semestersArray.getJSONObject(i);
                String semesterName = semesterObject.getString("semester_name");
                semestersList.add(semesterName);
            }

            semesterAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateSubjectList(int semesterPosition) {
        subjectsList.clear();

        try {
            JSONArray subjectsArray = jsonData.getJSONArray("courses").getJSONObject(spinnerCourse.getSelectedItemPosition())
                    .getJSONArray("semesters").getJSONObject(semesterPosition).getJSONArray("subjects");

            for (int i = 0; i < subjectsArray.length(); i++) {
                String subjectName = subjectsArray.getString(i);
                subjectsList.add(subjectName);
            }

            subjectAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}