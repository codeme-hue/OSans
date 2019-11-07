package id.kardihaekal.osans;

import static java.security.AccessController.getContext;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ChangePasswordActivity extends AppCompatActivity {

  private EditText editTextEmail;
  private EditText editTextPassword;
  private EditText editTextPassNew;
  private ProgressBar progressBar;
  private ProgressDialog mDialog;
  private Button buttonSave;

  private FirebaseAuth firebaseAuth;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_change_password);

    editTextEmail = (EditText) findViewById(R.id.edit_text_email_old_dialog_change_password);
    editTextPassword = (EditText) findViewById(R.id.edit_text_password_dialog_change_password);
    editTextPassNew = (EditText) findViewById(R.id.edit_text_password_new_dialog_change_password);
    buttonSave = (Button) findViewById(R.id.button_save_password_dialog_change_password);

    mDialog = new ProgressDialog(this);

    setTitle("Change Password");

    initFirebaseAuth();


  }

  private void initFirebaseAuth() {

    firebaseAuth = FirebaseAuth.getInstance();

  }

  public void buttonSave(View view) {

    String email = editTextEmail.getText().toString().trim();
    String password = editTextPassword.getText().toString().trim();
    final String passwordNew = editTextPassNew.getText().toString().trim();

    if (TextUtils.isEmpty(email)) {
      showMessageSnackbar("Email is empty!");
    } else if (TextUtils.isEmpty(password)) {
      showMessageSnackbar("Password is empty!");
    } else if (TextUtils.isEmpty(passwordNew)) {
      showMessageSnackbar("Please insert your password new!");
    } else {
      showProgress();
      firebaseAuth.signInWithEmailAndPassword(email, password)
          .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
              if (task.isSuccessful()) {
                firebaseAuth.getCurrentUser()
                    .updatePassword(passwordNew)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                        hideProgress();
                        if (task.isSuccessful()) {
                          AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChangePasswordActivity.this);
                          alertDialogBuilder.setTitle("Change Password");
                          alertDialogBuilder.setMessage("Your password has been changed! Please login again.");


                         /* AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChangePasswordActivity.this);
                          alertDialogBuilder.setTitle("Change Password");
                          alertDialogBuilder.setMessage("Your password has been changed! Please login again."); */
                          alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                              firebaseAuth.signOut();
                              Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                              startActivity(intent);
                              mDialog.setMessage("Please wait...");
                              mDialog.show();
                            }
                          });
                          alertDialogBuilder.show();
                        } else {
                          showMessageSnackbar("Your password fail to changed!");
                          mDialog.dismiss();
                        }
                      }
                    });
              } else {
                showMessageSnackbar("Your email and password is not matched!");
                mDialog.dismiss();
              }
            }
          });
    }
  }


  private void showProgress() {
    mDialog.show();
    buttonSave.setVisibility(View.GONE);
    editTextEmail.setEnabled(false);
    editTextPassword.setEnabled(false);
    editTextPassNew.setEnabled(false);
  }

  private void hideProgress() {
    mDialog.show();
    buttonSave.setVisibility(View.VISIBLE);
    editTextEmail.setEnabled(true);
    editTextPassword.setEnabled(true);
    editTextPassNew.setEnabled(true);
  }

  private void showMessageSnackbar(String message) {
    Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        .show();

  }
}
