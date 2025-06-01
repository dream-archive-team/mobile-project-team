package com.example.mobileteamapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DreamBasedNewNovActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dream_based_new_nov);

// 장르 라디오버튼 연결
        RadioButton cbRomance = findViewById(R.id.rbEndingOpen);
        RadioButton cbFantasy = findViewById(R.id.rbEndingHappy);
        RadioButton cbSF = findViewById(R.id.rbEndingTwist2);
        RadioButton cbAction = findViewById(R.id.rbEndingTwist3);
        RadioButton cbDocumentary = findViewById(R.id.rbEndingTwist);
        RadioButton cbThriller = findViewById(R.id.rbEndingGrowth);
        RadioButton cbComedy = findViewById(R.id.rbEndingSad);

// 모든 RadioButton을 비활성화 (사용자가 클릭할 수 없게)
        cbRomance.setEnabled(false);
        cbFantasy.setEnabled(false);
        cbSF.setEnabled(false);
        cbAction.setEnabled(false);
        cbDocumentary.setEnabled(false);
        cbThriller.setEnabled(false);
        cbComedy.setEnabled(false);

// 기타 뷰 연결
        TextView tvAnalysisLabel = findViewById(R.id.tvAnalysisLabel);
        TextView tvDreamAnalysis = findViewById(R.id.tvDreamAnalysis);
        TextView tvAnalysisLabel2 = findViewById(R.id.tvAnalysisLabel_2);
        EditText etDreamInput1 = findViewById(R.id.etDreamInput_1);
        Button btnHome = findViewById(R.id.btn_home);
        Button btnSave = findViewById(R.id.btn_save);
        Button btnSubmit = findViewById(R.id.btn_submit);

// SelectNovGenreActivity에서 전달받은 장르 정보 받기
        Intent intent = getIntent();
        String selectedGenre = intent.getStringExtra("selectedGenre");

// 전달받은 장르에 따라 해당 라디오버튼만 자동 체크
        if (selectedGenre != null) {
// 모든 라디오버튼을 먼저 해제
            cbRomance.setChecked(false);
            cbFantasy.setChecked(false);
            cbSF.setChecked(false);
            cbAction.setChecked(false);
            cbDocumentary.setChecked(false);
            cbThriller.setChecked(false);
            cbComedy.setChecked(false);

// 전달받은 장르와 일치하는 라디오버튼만 체크
            switch (selectedGenre) {
                case "로맨스":
                    cbRomance.setChecked(true);
                    break;
                case "판타지":
                    cbFantasy.setChecked(true);
                    break;
                case "SF":
                    cbSF.setChecked(true);
                    break;
                case "액션":
                    cbAction.setChecked(true);
                    break;
                case "다큐멘터리":
                    cbDocumentary.setChecked(true);
                    break;
                case "스릴러":
                    cbThriller.setChecked(true);
                    break;
                case "코미디":
                    cbComedy.setChecked(true);
                    break;
                default:
                    Toast.makeText(this, "알 수 없는 장르: " + selectedGenre, Toast.LENGTH_SHORT).show();
                    break;
            }

            Toast.makeText(this, "선택된 장르: " + selectedGenre, Toast.LENGTH_SHORT).show();
        }
    }
}