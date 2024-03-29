package com.example.letswritetogether.Activities;

import static com.example.letswritetogether.Utilities.Constants.LOGIN_FAILED;
import static com.example.letswritetogether.Utilities.Constants.UID_KEY;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import com.example.letswritetogether.R;
import com.example.letswritetogether.Utilities.MyDB;
import com.example.letswritetogether.Utilities.SignalGenerator;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private MaterialButton login_BTN_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
        initViews();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            moveToMainActivity(user);
    }

    private void initViews() {
        login_BTN_login.setOnClickListener(v-> {
            login();
        });
    }

    private void findViews() {
        login_BTN_login = findViewById(R.id.login_BTN_login);
    }

    private void login() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build());

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        signInLauncher.launch(signInIntent);
    }
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            moveToMainActivity(user);

        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            SignalGenerator.getInstance().toast(LOGIN_FAILED,Toast.LENGTH_SHORT);
        }
    }
    private void moveToMainActivity(FirebaseUser user) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(UID_KEY,user.getUid());
        startActivity(intent);
        finish();
    }
}
