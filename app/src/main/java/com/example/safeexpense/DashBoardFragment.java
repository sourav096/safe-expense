package com.example.safeexpense;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.safeexpense.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class DashBoardFragment extends Fragment {


    //Floting Button Initialization

    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    //Floting Button Textviewer

    private TextView fab_income_txt;
    private TextView fab_expense_txt;

    //Boolean
    private boolean isOpen = false;

    //Animation Class
    private Animation FadeOpen,FadeClose;

    //Firebase

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    //Dashboard income and expense

    private TextView totalIncomeResult;
    private TextView totalExpenseResult;

    //Recycler View
    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;


    public DashBoardFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview=inflater.inflate(R.layout.fragment_dash_board, container, false);
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();
        mIncomeDatabase= FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase=FirebaseDatabase.getInstance().getReference().child("ExpensewData").child(uid);


        //Connect floating button to layout..
        fab_main_btn=myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn=myview.findViewById(R.id.income_ft_btn);
        fab_expense_btn=myview.findViewById(R.id.expense_ft_btn);


        //Connect floating text..
        fab_income_txt=myview.findViewById(R.id.income_ft_text);
        fab_expense_txt=myview.findViewById(R.id.expense_ft_text);

        //Total income and expense result set
        totalIncomeResult=myview.findViewById(R.id.income_set_result);
        totalExpenseResult=myview.findViewById(R.id.expense_set_result);

        //Recycler
        mRecyclerIncome=myview.findViewById(R.id.recycler_income);
        mRecyclerExpense=myview.findViewById(R.id.recycler_expense);

        //Animation connect..
        FadeOpen= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        FadeClose= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);

        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addData();
                if(isOpen){
                    fab_income_btn.startAnimation(FadeClose);
                    fab_expense_btn.startAnimation(FadeClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(FadeClose);
                    fab_expense_txt.startAnimation(FadeClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);
                    isOpen=false;
                }else {
                    fab_income_btn.startAnimation(FadeOpen);
                    fab_expense_btn.startAnimation(FadeOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(FadeOpen);
                    fab_expense_txt.startAnimation(FadeOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen=true;
                }

            }
        });

        //Calculate total income
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int totalsum=0;

                for (DataSnapshot mysnap:dataSnapshot.getChildren()){
                    Data data = mysnap.getValue(Data.class);
                    totalsum+=data.getAmount();

                    String stResult=String.valueOf(totalsum);

                    totalIncomeResult.setText(stResult + ".00");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Calculate total Expense
        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int totalsum = 0;

                for (DataSnapshot mysnap : dataSnapshot.getChildren()) {
                    Data data = mysnap.getValue(Data.class);
                    totalsum += data.getAmount();

                    String stResult = String.valueOf(totalsum);

                    totalExpenseResult.setText(stResult + ".00");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Recycler

        LinearLayoutManager layoutManagerIncome = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);



        LinearLayoutManager layoutManagerExpense = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        layoutManagerExpense.setReverseLayout(true);
        layoutManagerExpense.setStackFromEnd(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);

        return myview;
    }

    //Floating button animation..
    private void ftAnimation(){
        if(isOpen){
            fab_income_btn.startAnimation(FadeClose);
            fab_expense_btn.startAnimation(FadeClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(FadeClose);
            fab_expense_txt.startAnimation(FadeClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);
            isOpen=false;
        }else {
            fab_income_btn.startAnimation(FadeOpen);
            fab_expense_btn.startAnimation(FadeOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(FadeOpen);
            fab_expense_txt.startAnimation(FadeOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen=true;
        }
    }
    private void addData(){

        //Fab button income..
        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               incomeDataInsert();
            }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseDataInsert();
            }
        });

    }

    public void incomeDataInsert(){
        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());

        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myview=inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myview);

        final AlertDialog dialog=mydialog.create();
        dialog.setCancelable(false);
        final EditText edtAmount=myview.findViewById(R.id.ammount_edt);
        final EditText edtType=myview.findViewById(R.id.type_edt);
        final EditText edtNote=myview.findViewById(R.id.note_edt);
        Button btnSave=myview.findViewById(R.id.btnSave);
        Button btnCancel=myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type=edtType.getText().toString().trim();
                String amount=edtAmount.getText().toString().trim();
                String note=edtNote.getText().toString().trim();

                if(TextUtils.isEmpty(type)){
                    edtType.setError("Required Field");
                    return;
                }
                if(TextUtils.isEmpty(amount)){
                    edtAmount.setError("Required Field");
                    return;
                }

                int ouramountint=Integer.parseInt(amount);

                if(TextUtils.isEmpty(note)){
                    edtNote.setError("Required Field");
                    return;
                }
                String id=mIncomeDatabase.push().getKey();
                String mDate= DateFormat.getDateInstance().format(new Date());
                Data data=new Data(ouramountint,type,note,id,mDate);
                mIncomeDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(),"Data Added",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                ftAnimation();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ftAnimation();
            }
        });
        dialog.show();
    }

    public void expenseDataInsert(){
        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myview=inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myview);

        final AlertDialog dialog=mydialog.create();
        dialog.setCancelable(false);
        final EditText amount=myview.findViewById(R.id.ammount_edt);
        final EditText type=myview.findViewById(R.id.type_edt);
        final EditText note=myview.findViewById(R.id.note_edt);

        Button btnSave=myview.findViewById(R.id.btnSave);
        Button btnCansel=myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmAmmount=amount.getText().toString().trim();
                String tmtype=type.getText().toString().trim();
                String tmnote=note.getText().toString().trim();

                if (TextUtils.isEmpty(tmAmmount)){
                    amount.setError("Required Fields...");
                    return;
                }

                int inamount=Integer.parseInt(tmAmmount);

                if (TextUtils.isEmpty(tmtype)){
                    type.setError("Required Fields...");
                    return;
                }
                if (TextUtils.isEmpty(tmnote)){
                    note.setError("Required Fields...");
                    return;
                }

                String id=mExpenseDatabase.push().getKey();
                String mDate=DateFormat.getDateInstance().format(new Date());

                Data data=new Data(inamount,tmtype,tmnote,id,mDate);
                mExpenseDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(),"Data added",Toast.LENGTH_SHORT).show();

                ftAnimation();
                dialog.dismiss();
            }
        });

        btnCansel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftAnimation();
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options=
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(mIncomeDatabase,Data.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter<Data,IncomeViewHolder> incomeAdapter=new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull IncomeViewHolder holder, int position, @NonNull Data model) {
                holder.setIncomeAmount(model.getAmount());
                holder.setIncomeType(model.getType());
                holder.setIncomeDate(model.getDate());
            }
            @NonNull
            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new IncomeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_income,parent,false));
            }
        };
        mRecyclerIncome.setAdapter(incomeAdapter);


        FirebaseRecyclerOptions<Data>option=
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(mExpenseDatabase,Data.class)
                        .setLifecycleOwner(this)
                        .build();
        FirebaseRecyclerAdapter<Data,ExpenseViewHolder>expenseAdapter=new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(option) {
            @Override
            protected void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position, @NonNull Data model) {
                holder.setExpenseAmount(model.getAmount());
                holder.setExpenseType(model.getType());
                holder.setExpenseDate(model.getDate());

            }

            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ExpenseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_expense,parent,false));
            }
        };
        mRecyclerExpense.setAdapter(expenseAdapter);



    }

    //For income data
    public static class IncomeViewHolder extends RecyclerView.ViewHolder{

        View mIncomeView;


        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            mIncomeView=itemView;
        }
        public void setIncomeAmount(int amount){
            TextView mAmount=mIncomeView.findViewById(R.id.amount_income_ds);
            String strAmount=String.valueOf(amount);
            mAmount.setText(strAmount);
        }
        public void setIncomeType(String type){
            TextView mType=mIncomeView.findViewById(R.id.type_income_ds);
            mType.setText(type);
        }
        public void setIncomeDate(String date){
            TextView mDate=mIncomeView.findViewById(R.id.date_income_ds);
            mDate.setText(date);
        }




    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{
        View mExpenseView;
        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            mExpenseView=itemView;
        }

        public void setExpenseAmount(int amount){
            TextView mAmount=mExpenseView.findViewById(R.id.amount_expense_ds);
            String strAmount=String.valueOf(amount);
            mAmount.setText(strAmount);
        }
        public void setExpenseType(String type){
            TextView mType=mExpenseView.findViewById(R.id.type_expense_ds);
            mType.setText(type);
        }
        public void setExpenseDate(String date){
            TextView mDate=mExpenseView.findViewById(R.id.date_expense_ds);
            mDate.setText(date);
        }



    }

}
