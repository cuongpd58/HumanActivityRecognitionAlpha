package com.uet.humanactivityrecognition.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.uet.humanactivityrecognition.R;
import com.uet.humanactivityrecognition.writers.UserWriter;

/**
 * Project name: HumanActivityRecognition
 * Created by Cuong Phan
 * Email: cuongphank58@gmail.com
 * on 20/02/2017.
 * University of Engineering and Technology,
 * Vietnam National University.
 */

public class NewUserDialog extends Dialog implements View.OnClickListener{
    private Context mContext;
    private UserWriter userWriter;
    private ImageButton imbCancel;
    private ImageButton imbSave;
    private EditText edtName;
    private RadioGroup rgSex;
    private RadioButton rbCheck;
    private EditText edtYOB;
    private EditText edtJob;

    private boolean isSaved;

    private String name;

    public NewUserDialog(Context context,UserWriter userWriter) {
        super(context);
        initComps(context, userWriter);
        initViews();
    }

    private void initViews() {
        imbCancel = (ImageButton)findViewById(R.id.imb_close_add_user);
        imbCancel.setOnClickListener(this);
        imbSave = (ImageButton)findViewById(R.id.imb_save_add_user);
        imbSave.setOnClickListener(this);
        edtName = (EditText) findViewById(R.id.edt_username);
        rgSex = (RadioGroup) findViewById(R.id.rg_sex);
        edtYOB = (EditText) findViewById(R.id.edt_year_of_birth);
        edtJob = (EditText)findViewById(R.id.edt_job);

        isSaved = false;
    }

    private void initComps(Context mContext, UserWriter userWriter) {
        this.mContext = mContext;
        this.userWriter = userWriter;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_user_dialog_layout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imb_close_add_user:
                dismiss();
                break;
            case R.id.imb_save_add_user:
                saveUser();
                break;
            default:
                break;
        }
    }

    public void saveUser() {
        //Check infor
        String fullname = edtName.getText().toString();
        if (fullname.isEmpty()){
            edtName.setError(mContext.getResources().getString(R.string.edt_name_warning));
            return;
        }

        int sexId = rgSex.getCheckedRadioButtonId();
        if (sexId == -1){
            Toast.makeText(mContext,mContext.getResources().getString(R.string.please_choose_sex),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String yearOfBirth = edtYOB.getText().toString();
        if (yearOfBirth.isEmpty()){
            edtYOB.setError(mContext.getResources().getString(R.string.edt_yob_warning));
            return;
        }

        String job = edtJob.getText().toString();
        if (job.isEmpty()){
            edtJob.setError(mContext.getResources().getString(R.string.edt_job_warning));
            return;
        }

        rbCheck = (RadioButton)rgSex.findViewById(rgSex.getCheckedRadioButtonId());
        userWriter.addUserData(fullname,rbCheck.getText().toString(),
                Integer.parseInt(yearOfBirth),job);
        isSaved = true;
        name = edtName.getText().toString();
        dismiss();
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public String getName() {
        return name;
    }
}
