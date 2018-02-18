package me.xp090.secretshat;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kevalpatel.passcodeview.PasscodeView;
import com.kevalpatel.passcodeview.interfaces.AuthenticationListener;

import me.xp090.secretshat.Util.PasscodeUtil;

import static me.xp090.secretshat.Util.SharedPreferencesUtil.SecOptions;

public class SecurityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        Fragment fragment = null;
        String mode = getIntent().getStringExtra(PasscodeUtil.SECURITY_MODE);

        if (mode.equals(PasscodeUtil.MODE_RECORDING)) {
            fragment = new PasscodeRecordingFragment();
        } else if (mode.equals(PasscodeUtil.MODE_LOCK)) {
            fragment = new PasscodLockFragment();
        } else {
            throw new RuntimeException("No mode selected");
        }
        getFragmentManager().beginTransaction()
                .replace(R.id.security_container, fragment)
                .commit();
    }
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////

    public static class PasscodeRecordingFragment extends Fragment {
        PasscodeView passcodeView;
        Button nextButton;
        Button backButton;


        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

            if (SecOptions.SecurityMethod.equals("pattern")) {
                return inflater.inflate(R.layout.fragment_pattern, container, false);
            } else if (SecOptions.SecurityMethod.equals("pin")) {
                return inflater.inflate(R.layout.fragment_pin, container, false);
            }
            return super.onCreateView(inflater, container, savedInstanceState);

        }

        @Override
        public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            nextButton = view.findViewById(R.id.btn_next);
            backButton = view.findViewById(R.id.btn_back);

            passcodeView = view.findViewById(R.id.passcode_view);
            PasscodeUtil.preparePasscode(getActivity(), passcodeView);
            setupPasscodeRecording();

        }

        private void setupPasscodeRecording() {
            nextButton.setEnabled(false);
            nextButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            passcodeView.setRecording(true);
            PasscodeUtil.prepareEntringPasscode(passcodeView);
            passcodeView.setAuthenticationListener(new AuthenticationListener() {
                @Override
                public void onAuthenticationSuccessful() {
                    nextButton.setEnabled(true);
                    nextButton.setBackgroundColor(getResources().getColor(R.color.secAccentDark));
                    nextButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setupPasscodeConfirm(passcodeView.getTypedPasscode());
                        }
                    });
                }

                @Override
                public void onAuthenticationFailed() {
                    nextButton.setEnabled(false);
                    nextButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }
            });
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });

        }

        private void setupPasscodeConfirm(int[][] passcode) {
            nextButton.setEnabled(false);
            nextButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            PasscodeUtil.prepareConfirmingPasscode(passcodeView, passcode);
            passcodeView.setAuthenticationListener(new AuthenticationListener() {
                @Override
                public void onAuthenticationSuccessful() {
                    nextButton.setEnabled(true);
                    nextButton.setBackgroundColor(getResources().getColor(R.color.secAccentDark));
                    nextButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SecOptions.PassCode = passcodeView.getTypedPasscode();
                            getActivity().setResult(RESULT_OK);
                            getActivity().finish();
                        }
                    });

                }

                @Override
                public void onAuthenticationFailed() {
                    nextButton.setEnabled(false);
                    nextButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }
            });
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setupPasscodeRecording();
                }
            });

        }

        @Override
        public void onDestroy() {
            passcodeView.setAuthenticationListener(null);
            super.onDestroy();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////

    public static class PasscodLockFragment extends Fragment {
        PasscodeView passcodeView;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

            if (SecOptions.SecurityMethod.equals("pattern")) {
                return inflater.inflate(R.layout.fragment_pattern, container, false);
            } else if (SecOptions.SecurityMethod.equals("pin")) {
                return inflater.inflate(R.layout.fragment_pin, container, false);
            }
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            passcodeView = view.findViewById(R.id.passcode_view);
            View navButtons = view.findViewById(R.id.passcode_navigation_buttons);
            navButtons.setVisibility(View.GONE);
            PasscodeUtil.preparePasscodeLock(getActivity(), passcodeView);

            passcodeView.setAuthenticationListener(new AuthenticationListener() {
                @Override
                public void onAuthenticationSuccessful() {
                    getActivity().setResult(RESULT_OK);
                    getActivity().finish();
                }

                @Override
                public void onAuthenticationFailed() {

                }
            });


        }

    }

}