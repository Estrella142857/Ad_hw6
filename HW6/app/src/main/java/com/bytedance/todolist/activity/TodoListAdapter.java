package com.bytedance.todolist.activity;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bytedance.todolist.R;
import com.bytedance.todolist.database.TodoListEntity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wangrui.sh
 * @since Jul 11, 2020
 */
public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.TodoListItemHolder> implements View.OnClickListener {
    public List<TodoListEntity> mDatas;

    public TodoListAdapter() {
        mDatas = new ArrayList<>();
    }
    @NonNull
    @Override
    public TodoListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TodoListItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TodoListItemHolder holder, int position) {
        holder.bind(mDatas.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @MainThread
    public void setData(List<TodoListEntity> list) {
        mDatas = list;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (!(v instanceof CheckBox || v instanceof ImageButton)) return;
        int position = (int) v.getTag();
        if (null != mOnItemClickListener) {
            switch (v.getId()){
                case R.id.checkbox:
                    mOnItemClickListener.onItemClick(v, ViewName.CHECKBOX, position);
                    break;
                case R.id.btn_delete:
                    mOnItemClickListener.onItemClick(v, ViewName.DELETE_ICON, position);
            }
        }
    }

    public enum ViewName { CHECKBOX, DELETE_ICON }
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View v, ViewName viewName, int position);
    }

    public void setOnItemClickListener (OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public class TodoListItemHolder extends RecyclerView.ViewHolder {
        private TextView mContent;
        private TextView mTimestamp;
        private CheckBox mFinished;
        private ImageButton mDelete;

        public TodoListItemHolder(@NonNull View itemView) {
            super(itemView);
            mContent = itemView.findViewById(R.id.tv_content);
            mTimestamp = itemView.findViewById(R.id.tv_timestamp);
            mFinished = itemView.findViewById(R.id.checkbox);
            mDelete = itemView.findViewById(R.id.btn_delete);

            itemView.setOnClickListener(TodoListAdapter.this);
            mContent.setOnClickListener(TodoListAdapter.this);
            mTimestamp.setOnClickListener(TodoListAdapter.this);
            mFinished.setOnClickListener(TodoListAdapter.this);
            mDelete.setOnClickListener(TodoListAdapter.this);
        }

        public void bind(TodoListEntity entity, int position) {
            mContent.setText(entity.getContent());
            mTimestamp.setText(formatDate(entity.getTime()));
            if (0L!=entity.getState()) {
                mFinished.setChecked(true);
                mContent.setTextColor(Color.GRAY);
                mContent.setPaintFlags(mContent.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                mContent.setTextColor(Color.BLACK);
                mContent.setPaintFlags(mContent.getPaintFlags()&(~Paint.STRIKE_THRU_TEXT_FLAG));
                mFinished.setChecked(false);
            }
            mContent.setTag(position);
            mTimestamp.setTag(position);
            mFinished.setTag(position);
            mDelete.setTag(position);
        }

        private String formatDate(Date date) {
            DateFormat format = SimpleDateFormat.getDateInstance();
            return format.format(date);
        }
    }

}
