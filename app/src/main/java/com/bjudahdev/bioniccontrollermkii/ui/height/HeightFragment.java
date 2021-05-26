package com.bjudahdev.bioniccontrollermkii.ui.height;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bjudahdev.bioniccontrollermkii.R;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class HeightFragment extends Fragment {

    private HeightViewModel heightViewModel;
    int duration=Toast.LENGTH_SHORT;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        heightViewModel =
                new ViewModelProvider(this).get(HeightViewModel.class);
        View root = inflater.inflate(R.layout.fragment_height, container, false);

        //init for slider and slider listener
        Slider slider = root.findViewById(R.id.height_slider);
        slider.addOnSliderTouchListener(touchListener);

        TextInputEditText height_editText = root.findViewById(R.id.height_Number_Input);

        return root;
    }

    //text listener
    private final TextInputLayout.OnEditTextAttachedListener textListener = new TextInputLayout.OnEditTextAttachedListener() {
        @Override
        public void onEditTextAttached(@NonNull TextInputLayout textInputLayout) {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.text_edit, duration);
            toast.show();
        }
    };

    //slider listeners
    private final Slider.OnSliderTouchListener touchListener = new Slider.OnSliderTouchListener() {
        private Slider slider;

        @Override
        public void onStartTrackingTouch(Slider slider) {
           // Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.slider_start, duration);
            // toast.show();
        }

        @Override
        public void onStopTrackingTouch(Slider slider) {
            //Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.slider_end, duration);
            //toast.show();
            float slider_value = slider.getValue();
            setSliderTextBox(slider_value);
        }
    };

    public void setSliderTextBox(float value){
        TextInputLayout textInputLayout = getView().findViewById(R.id.height_Number);
        textInputLayout.getEditText().setText(Float.toString(value));
    }

    public void setSliderValue(float value){
        Slider heightSlider = getView().findViewById(R.id.height_slider);
        heightSlider.setValue(value);
    }
}