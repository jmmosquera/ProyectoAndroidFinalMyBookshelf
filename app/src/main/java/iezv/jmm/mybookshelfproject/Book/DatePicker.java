package iezv.jmm.mybookshelfproject.Book;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

import iezv.jmm.mybookshelfproject.Book.AddBook;

public class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener{


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Usa la fecha actual como predeterminada en el DatePicker.
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Crea una nueva instancia del DatePicker y la devuelve.
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
        //Retorna la fecha y la devuelve, llamando a los métedos que la establecerán en la pantalla AddBook.
        String date = dayOfMonth+"/"+(month+1)+"/"+year;
        String tag = getTag();
        if(tag.equals("datepicker1")){
            ((AddBook) getActivity()).getDate(date);
        }else if(tag.equals("datepicker2")){
            ((AddBook) getActivity()).getEndDate(date);
        }

    }

    //Al cerrar el DatePicker, llama a la función que cambia el color del texto unos segundos.
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((AddBook) getActivity()).changeColorBriefly();

    }
}
