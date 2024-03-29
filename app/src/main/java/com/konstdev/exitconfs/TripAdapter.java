package com.konstdev.exitconfs;
import android.view.View;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TripAdapter extends ArrayAdapter<Trip> {

    public TripAdapter(Context context, ArrayList<Trip> trips) {
        super(context, 0, trips);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Получаем объект по позиции
        Trip trip = getItem(position);

        // Проверяем, используется ли представление; если нет, загружаем его
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.petek_inlist, parent, false);
        }

        // Находим TextView в макете элемента списка и устанавливаем значения атрибутов
        TextView textViewDepartureTime = convertView.findViewById(R.id.textViewDepartureTime);
        TextView textViewReturnTime = convertView.findViewById(R.id.textViewReturnTime);
        TextView textViewDestination = convertView.findViewById(R.id.textViewDestination);
        TextView textViewStatus = convertView.findViewById(R.id.textViewStatus);
        TextView textViewDepartureDate = convertView.findViewById(R.id.textViewDepartureDate);
        TextView textViewReturnDate = convertView.findViewById(R.id.textViewReturnDate);

        textViewDepartureTime.setText("Departure Time: " + trip.getExitTime());
        textViewReturnTime.setText("Return Time: " + trip.getReturnTime());
        textViewDestination.setText("Destination: " + trip.getGoingTo());
        textViewStatus.setText("Confirmed: " + trip.isConfirmed());
        textViewDepartureDate.setText("Departure Date: " + trip.getExitDate());
        textViewReturnDate.setText("Return Date: " + trip.getReturnDate());

        return convertView;
    }
}