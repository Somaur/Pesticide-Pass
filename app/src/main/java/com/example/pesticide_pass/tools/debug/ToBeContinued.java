package com.example.pesticide_pass.tools.debug;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

public class ToBeContinued {
    static void alert(Context c) {
        Toast.makeText(c,"功能尚未完成", Toast.LENGTH_SHORT).show();
    }
    public static View.OnClickListener clickListener = new Click();
    public static class Click implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            alert(view.getContext());
        }
    }
}
