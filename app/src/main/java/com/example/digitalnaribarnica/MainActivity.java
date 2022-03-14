package com.example.digitalnaribarnica;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.database.User;
import com.example.database.Utils.SHA256;
import com.example.digitalnaribarnica.Fragments.HomeFragment;
import com.example.digitalnaribarnica.ViewModel.SharedViewModel;
import com.example.digitalnaribarnica.databinding.ActivityMainBinding;
import com.example.repository.Listener.FirestoreCallback;
import com.example.repository.Repository;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView registracija, zaboravljenaLozinka;

    ActivityMainBinding binding;
    SignInButton signin;
    LoginButton loginButton;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN=0;
    int SIGN_IN_FB=1;

    private ImageView btnLanguage;
    private View view;
    private EditText email;
    private TextInputEditText password;
    private Boolean vrijemeTece = false;
    private SharedViewModel sharedViewModel;

    CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding=ActivityMainBinding.inflate((getLayoutInflater()));
        View view=binding.getRoot();
        setContentView(view);
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        registracija = binding.registracijaButton;
        btnLanguage = binding.btnLanguage;
        email = binding.emailEDIT;
        password = binding.passwordEDIT;
        email.setText("");
        password.setText("");

        btnLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] items = {getString(R.string.croatian), getString(R.string.english)};

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.chooseLanguage));
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                updateResources(MainActivity.this, "");
                                finish();
                                startActivity(getIntent());
                                break;

                            case 1:
                                updateResources(MainActivity.this, "en");
                                finish();
                                startActivity(getIntent());
                                break;
                        }
                    }
                });
                builder.show();
            }
        });

        registracija.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (MainActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        zaboravljenaLozinka = findViewById(R.id.tvForgot);
        zaboravljenaLozinka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (MainActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });


        FacebookSdk.sdkInitialize(MainActivity.this);
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();

        LoginButton loginButton =binding.loginButton;
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });

        signin=binding.signInButton;
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                    // ...
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.btnPrijava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.emailEDIT.length()!=0 && binding.passwordEDIT.length()!=0) {
                    mAuth.signInWithEmailAndPassword(binding.emailEDIT.getText().toString(),
                            binding.passwordEDIT.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if (mAuth.getCurrentUser().isEmailVerified()) {
                                    startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                                } else {
                                    Toast.makeText(MainActivity.this, getString(R.string.pleaseConfirmEmail), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(MainActivity.this, getString(R.string.checkSignInData), Toast.LENGTH_SHORT).show();
                }

                Repository repository=new Repository();

                sharedViewModel.DohvatiKorisnikaPoEmailu(binding.emailEDIT.getText().toString());
                sharedViewModel.userMutableLiveData.observe(MainActivity.this, new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        FirebaseUser fUser = mAuth.getCurrentUser();
                        if(fUser != null && mAuth.getCurrentUser().isEmailVerified()) {

                            if (user != null) {
                                if (!user.getBlokiran()) {
                                    String s1 = binding.passwordEDIT.getText().toString();

                                    try {
                                            if (sharedViewModel.ProvjeriPassword(user.getPassword(), SHA256.toHexString(SHA256.getSHA(s1)))) {
                                            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                                            intent.putExtra("CurrentUser", user);
                                            MainActivity.this.startActivity(intent);

                                        } else
                                            showToast(view, getString(R.string.wrongPassword));
                                    } catch (NoSuchAlgorithmException e) {
                                        showToast(view, getString(R.string.cantComputeSHA256));
                                    }
                                } else
                                    showToast(view, getString(R.string.userBlocked));
                            } else
                                showToast(view, getString(R.string.userNotFound));
                        }
                    }
                });
            }
        });
    }

    public void showToast(View v, String poruka){
        if(!vrijemeTece){
            vrijemeTece = true;
            StyleableToast.makeText(this, poruka, 3, R.style.Toast).show();
            new CountDownTimer(3000, 1000) {
                public void onTick(long millisUntilFinished) {
                }
                public void onFinish() {
                    vrijemeTece = false;
                }
            }.start();
        }
    }

    public void saveInformation(String username,String password) {
        SharedPreferences shared = getSharedPreferences("shared", MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.commit();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    String GoogleUserID, GoogleUserName, GoogleUserEmail, GoogleUserPhoto;
    Uri GoogleUserPhotoURI;

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Intent intent =new Intent(MainActivity.this,RegisterActivity.class);
            startActivity(intent);
            if (account != null) {
                if (account.getId() != null) {
                    GoogleUserID = account.getId();
                }
                if (account.getDisplayName() != null) {
                    GoogleUserName = account.getDisplayName();
                }
                if (account.getEmail() != null) {
                    GoogleUserEmail = account.getEmail();
                }
                if (account.getPhotoUrl() != null) {
                    GoogleUserPhotoURI = account.getPhotoUrl();
                    GoogleUserPhoto = GoogleUserPhotoURI.toString();
                }
                else if (account.getPhotoUrl() == null){
                    GoogleUserPhoto = "https://firebasestorage.googleapis.com/v0/b/digitalna-ribarnica-fb.appspot.com/o/default_profilna%2Fuser_image.jpg?alt=media&token=e30a1426-9be2-40d8-8e5a-b5e4c43337e7";
                }
                Repository repository = new Repository();
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("Users").whereEqualTo("userID", GoogleUserID)
                        .limit(1).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    boolean isEmpty = task.getResult().isEmpty();
                                    if(isEmpty) {
                                        try {
                                            sharedViewModel.DodajKorisnikaUBazuBezLozinke(GoogleUserID, GoogleUserName, GoogleUserEmail, GoogleUserPhoto);
                                            Intent intent = new Intent(MainActivity.this, OnboardingActivity.class);
                                            startActivity(intent);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        });
            }
        } catch (ApiException e) {
            Log.w("ERROR", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            updateUI(currentUser);
        }
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null){
            Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
            startActivity(intent);
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Toast.makeText(MainActivity.this, getString(R.string.authenticationFailed),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if(user !=null && user.isEmailVerified()){
            Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, getString(R.string.pleaseSignIn), Toast.LENGTH_SHORT).show();
        }
    }
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getString(R.string.wantToExit));
            alertDialogBuilder
                    .setMessage(getString(R.string.clickYesToExit))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    moveTaskToBack(true);
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                    System.exit(1);
                                }
                            })

                    .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        this.doubleBackToExitPressedOnce = true;

        showToast(view, getString(R.string.nameTooBig));

        Toast.makeText(this, getString(R.string.pressBackTwo), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    public static void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

}