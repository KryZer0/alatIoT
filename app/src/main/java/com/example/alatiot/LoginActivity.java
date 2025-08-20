package com.example.alatiot;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
public class LoginActivity extends AppCompatActivity {
    private EditText editTextUsername, editTextPassword;
    private Button loginGuessBtn;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.loginButton);
        loginGuessBtn = findViewById(R.id.loginGuess);

        buttonLogin.setOnClickListener(v -> loginUser());
        loginGuessBtn.setOnClickListener(v -> loginGuess());
        cekGooglePlayServices();
        // Fungsi mengirim data random ke firebase
        // Untuk testing firebase
//      firebaseHelper.kirimDataRandomKeFirebase();
    }
    private void cekGooglePlayServices() {
        GoogleApiAvailability gms = GoogleApiAvailability.getInstance();
        int status = gms.isGooglePlayServicesAvailable(this);

        if (status == ConnectionResult.SUCCESS) {
            Toast.makeText(this, "✅ Google Play Services tersedia & up-to-date", Toast.LENGTH_LONG).show();
        } else if (gms.isUserResolvableError(status)) {
            Toast.makeText(this, "⚠ Google Play Services ada tapi perlu update/izin", Toast.LENGTH_LONG).show();
            gms.getErrorDialog(this, status, 2404).show();
        } else {
            Toast.makeText(this, "❌ Google Play Services tidak tersedia di device ini", Toast.LENGTH_LONG).show();
        }
    }
    private void loginUser() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username dan password tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteOperations sqliteOperations = new SQLiteOperations(this);
        UserModel user = sqliteOperations.getUser(username, password);

        if (user != null) {
            Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show();

            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            String role = user.getRole();

            // Kirim ke Activity berikutnya
            Intent intent = new Intent(LoginActivity.this, menuActivity.class);
            intent.putExtra("login", "user");
            intent.putExtra("role", role);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Username atau password salah!", Toast.LENGTH_SHORT).show();
        }
    }


    private void loginGuess() {
        Intent intent = new Intent(LoginActivity.this, menuActivity.class);
        intent.putExtra("login","guess");
        startActivity(intent);
    }
}