package me.xp090.secretshat.Util;

import android.content.Context;

import com.kevalpatel.passcodeview.KeyNamesBuilder;
import com.kevalpatel.passcodeview.PasscodeView;
import com.kevalpatel.passcodeview.PatternView;
import com.kevalpatel.passcodeview.PinView;
import com.kevalpatel.passcodeview.indicators.CircleIndicator;
import com.kevalpatel.passcodeview.keys.RoundKey;
import com.kevalpatel.passcodeview.patternCells.DotPatternCell;

import me.xp090.secretshat.R;

import static me.xp090.secretshat.Util.SharedPreferencesUtil.SecOptions;

/**
 * Created by Xp090 on 31/12/2017.
 */

public class PasscodeUtil {
    public static final String SECURITY_MODE = "SecurityMode";
    public static final String MODE_RECORDING = "recoding";
    public static final String MODE_LOCK = "lock";

    public static void preparePasscode(Context context, PasscodeView passcodeView) {
        if (SecOptions.SecurityMethod.equals("pattern")) {
            final PatternView patternView = (PatternView) passcodeView;
            patternView.setNoOfColumn(SecOptions.PatternSize);
            patternView.setNoOfRows(SecOptions.PatternSize);
            patternView.setPatternCell(new DotPatternCell.Builder(patternView)
                    .setRadius(R.dimen.pattern_cell_radius)
                    .setCellColorResource(R.color.icons)
                    .build());
            patternView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    patternView.requestLayout();
                    patternView.invalidate();
                }
            }, 100);

        } else if (SecOptions.SecurityMethod.equals("pin")) {
            final PinView pinView = (PinView) passcodeView;
            pinView.setPinLength(SecOptions.PinLength);


            pinView.setKey(new RoundKey.Builder(pinView)
                    .setKeyPadding(R.dimen.key_padding)
                    .setKeyStrokeColorResource(R.color.icons)
                    .setKeyStrokeWidth(R.dimen.key_stroke_width)
                    .setKeyTextColorResource(R.color.icons)
                    .setKeyTextSize(R.dimen.key_text_size)
                    .build());

            float scale = context.getResources().getDisplayMetrics().density;
            float radius = context.getResources().getDimension(R.dimen.indicator_radius);
           /* if (SecOptions.PinLength > 6) {
                int inc = SecOptions.PinLength - 6 ;
                radius = radius - (inc * scale * 2);

            }*/
            pinView.setIndicator(new CircleIndicator.Builder(pinView)
                    .setIndicatorRadius(radius)
                    .setIndicatorFilledColorResource(R.color.icons)
                    .setIndicatorStrokeColorResource(R.color.icons)
                    .setIndicatorStrokeWidth(R.dimen.indicator_stroke_width)
                    .build());

            pinView.setKeyNames(new KeyNamesBuilder()
                    .setKeyOne("1")
                    .setKeyTwo("2")
                    .setKeyThree("3")
                    .setKeyFour("4")
                    .setKeyFive("5")
                    .setKeySix("6")
                    .setKeySeven("7")
                    .setKeyEight("8")
                    .setKeyNine("9")
                    .setKeyZero("0"));
            pinView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pinView.requestLayout();
                    pinView.invalidate();

                }
            }, 100);
        }
    }

    public static void prepareEntringPasscode(PasscodeView passcodeView) {
        passcodeView.setIsFingerPrintEnable(false);
        passcodeView.reset();
        if (SecOptions.SecurityMethod.equals("pattern")) {
            passcodeView.setTitle("Draw your pattern");
        } else if (SecOptions.SecurityMethod.equals("pin")) {
            passcodeView.setTitle("Enter your pin");
        }
    }

    public static void prepareConfirmingPasscode(PasscodeView passcodeView, int[][] passcode) {
        passcodeView.setRecording(false);
        passcodeView.setCorrectPasscode(passcode);
        passcodeView.reset();
        if (SecOptions.SecurityMethod.equals("pattern")) {
            passcodeView.setTitle("Draw your pattern again");
        } else if (SecOptions.SecurityMethod.equals("pin")) {
            passcodeView.setTitle("Enter your pin again");
        }
    }

    public static void preparePasscodeLock(Context context, PasscodeView passcodeView) {
        preparePasscode(context, passcodeView);
        prepareEntringPasscode(passcodeView);
        passcodeView.setRecording(false);
        passcodeView.setIsFingerPrintEnable(true);
        passcodeView.setCorrectPasscode(SecOptions.PassCode);
    }
}
