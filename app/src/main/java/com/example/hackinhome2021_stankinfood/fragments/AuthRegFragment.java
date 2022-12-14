package com.example.hackinhome2021_stankinfood.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.hackinhome2021_stankinfood.R;
import com.example.hackinhome2021_stankinfood.activities.MainActivity;
import com.google.android.gms.common.SignInButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class AuthRegFragment extends Fragment implements
        View.OnClickListener {
    private static final String IS_REGISTRATION = "isRegistration";

    private boolean isRegistration;

    private TextView textViewAuthRegTitle;
    private EditText editTextEmail;
    private ImageButton imageButtonViewPassword;
    private EditText editTextPassword;
    private Button buttonRequest;
    private TextView textViewForgotPassword;
    private SignInButton buttonRequestGoogle;

    public AuthRegFragment() {
    }

    public static AuthRegFragment newInstance(Boolean isRegistration) {
        AuthRegFragment fragment = new AuthRegFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_REGISTRATION, isRegistration);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isRegistration = getArguments().getBoolean(IS_REGISTRATION);
        }
        if (savedInstanceState != null) {
            isRegistration = savedInstanceState.getBoolean(IS_REGISTRATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auth_reg, container, false);

        initTextViews(view);
        initButtons(view);
        initImageButton(view);
        initEditTexts(view);

        return view;
    }

    private void initTextViews(View view) {
        textViewAuthRegTitle = view.findViewById(R.id.textViewAuthRegTitle);
        textViewForgotPassword = view.findViewById(R.id.textViewForgotPassword);

        if (isRegistration) {
            textViewAuthRegTitle.setText(getResources().getString(R.string.registration));
            textViewForgotPassword.setVisibility(View.GONE);
        } else {
            textViewAuthRegTitle.setText(getResources().getString(R.string.authorization));
            textViewForgotPassword.setOnClickListener(this);
        }
    }

    private void initButtons(View view) {
        buttonRequest = view.findViewById(R.id.buttonRequest);
        buttonRequestGoogle = view.findViewById(R.id.buttonRequestGoogle);

        if (isRegistration) {
            buttonRequest.setText(getResources().getString(R.string.sign_up));
        } else {
            buttonRequest.setText(getResources().getString(R.string.sign_in));
        }

        buttonRequest.setOnClickListener(this);
        buttonRequestGoogle.setOnClickListener(this);
    }

    private void initImageButton(View view) {
        imageButtonViewPassword = view.findViewById(R.id.imageButtonViewPassword);
        imageButtonViewPassword.setSelected(false);
        imageButtonViewPassword.setOnClickListener(this);
    }

    private void initEditTexts(View view) {
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);

        if (isRegistration) {
            editTextPassword.setHint(getResources().getString(R.string.password_reg_hint));
        } else {
            editTextPassword.setHint(getResources().getString(R.string.password_auth_hint));
        }
    }

    private boolean isEmailCorrect() {
        String email = editTextEmail.getText().toString();
        return email.contains("@") && email.contains(".");
    }

    private boolean isPasswordCorrect() {
        String password = editTextPassword.getText().toString();
        return password.length() >= 8 && !password.contains(" ");
    }

    private void showAlertDialogForgotPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.reset_password_title));
        builder.setMessage(getResources().getString(R.string.reset_password_message));
        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                String email = editTextEmail.getText().toString();
                ((MainActivity) getActivity()).sendResetPasswordByEmail(email);
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), (dialog, id) -> {
        });
        builder.setCancelable(true);
        builder.create();
        builder.show();
    }


    public void showAlertDialogVerificationMessage(String email) {
        String title = getResources().getString(R.string.verification_email_title);
        String message = getResources().getString(R.string.verification_email_message) + "\n" + email;
        String buttonOkString = getResources().getString(R.string.ok);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(buttonOkString, (dialog, id) -> {
            ((MainActivity) getActivity()).replaceFragmentToAuthRegFragment(false);
        });
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }

    public void showSnackBarEmailNotVerified() {
        Snackbar.make(getView(),
                getResources().getString(R.string.error_verify_email),
                BaseTransientBottomBar.LENGTH_LONG).show();
    }

    private void hidePassword(boolean hide) {
        if (hide) {
            editTextPassword.setTransformationMethod(new PasswordTransformationMethod());
        } else {
            editTextPassword.setTransformationMethod(null);
        }
        imageButtonViewPassword.setSelected(!imageButtonViewPassword.isSelected());
        editTextPassword.setSelection(editTextPassword.getText().toString().length());
    }

    public void showSnackBarResetPassword(String email) {
        Snackbar.make(getView(),
                getResources().getString(R.string.reset_password) + " " + email,
                BaseTransientBottomBar.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.buttonRequest) {
            if (isEmailCorrect()) {
                if (isPasswordCorrect()) {
                    String email = editTextEmail.getText().toString();
                    String password = editTextPassword.getText().toString();

                    if (isRegistration) {
                        ((MainActivity) getActivity()).createUserWithEmailAndPassword(email, password);
                    } else {
                        ((MainActivity) getActivity()).authUserWithEmailAndPassword(email, password);
                    }
                    //TODO Зарегистрировать или авторизировать
                } else {
                    //TODO можно доработать анимацию
                    Toast.makeText(getContext(),
                            getResources().getString(R.string.incorrect_password),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                //TODO можно доработать анимацию
                Toast.makeText(getContext(),
                        getResources().getString(R.string.incorrect_email),
                        Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.buttonRequestGoogle) {
            ((MainActivity) getActivity()).signInWithGoogle();
        } else if (id == R.id.textViewForgotPassword) {
            if (isEmailCorrect()) {
                showAlertDialogForgotPassword();
            } else {
                Toast.makeText(getContext(),
                        getResources().getString(R.string.incorrect_email),
                        Toast.LENGTH_SHORT).show();
            }
        }
        if (id == R.id.imageButtonViewPassword) {
            hidePassword(imageButtonViewPassword.isSelected());
        }
    }
}
