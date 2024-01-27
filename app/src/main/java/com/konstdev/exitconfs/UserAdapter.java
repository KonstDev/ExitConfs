package com.konstdev.exitconfs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

// адаптер для работы с RecyclerView предназначенный для отображения списка пользователей
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private ArrayList<User> userList;

    private ArrayList<User> selectedUsers;

    public UserAdapter(ArrayList<User> userList) {
        this.userList = userList;
        this.selectedUsers = new ArrayList<>(); // инициализация списка выбранных пользователей
    }

    // создание нового ViewHolder при необходимости
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Создание View из макета item_user
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view); // возвращение нового экземпляра UserViewHolder
    }

    // привязка данных пользователя к ViewHolder при прокрутке списка
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        // Получение пользователя по позиции
        User user = userList.get(position);
        holder.checkBox.setText(user.getName());
        // сброс слушателя перед установкой нового значения
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(selectedUsers.contains(user));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedUsers.add(user);
            } else {
                selectedUsers.remove(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public List<User> getSelectedUsers() {
        return selectedUsers;
    }

    // ViewHolder для хранения View элемента списка пользователей
    public class UserViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox; // CheckBox для выбора пользователя

        public UserViewHolder(@NonNull View itemView) {
            super(itemView); //вызов родительского класса
            checkBox = itemView.findViewById(R.id.checkboxUser); // Инициализация CheckBox из макета
        }
    }
}
