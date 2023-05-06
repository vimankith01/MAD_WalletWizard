package com.example.walletwizard.Activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.walletwizard.Models.expensesModel
import com.example.walletwizard.R
import com.example.walletwizard.R.id.ettype
import com.google.firebase.database.FirebaseDatabase

class ExpensesDetailsActivity : AppCompatActivity() {
    private lateinit var tvamount: TextView
    private lateinit var tvtype: TextView
    private lateinit var tvnote: TextView
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window. FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar()?.hide();//This Line hides the action bar

        setContentView(R.layout.activity_expense_update)

        initView()
        setValuesToViews()

        btnUpdate.setOnClickListener {
            openUpdateDialog(
                intent.getStringExtra("empId").toString()
            )
        }

        btnDelete.setOnClickListener {
            deleteRecord(
                intent.getStringExtra("empId").toString()
            )
        }

    }

    private fun initView() {
        tvamount = findViewById(R.id.tvamount)
        tvtype = findViewById(R.id.tvtype)
        tvnote = findViewById(R.id.tvnote)

        btnUpdate = findViewById(R.id.btnUpdate_expense)
        btnDelete = findViewById(R.id.btnDelete_expense)
    }

    private fun setValuesToViews() {
        tvamount.text = intent.getStringExtra("expAmount")
        tvtype.text = intent.getStringExtra("expType")
        tvnote.text = intent.getStringExtra("expNote")

    }

    private fun deleteRecord(
        id: String
    ){
        val dbRef = FirebaseDatabase.getInstance().getReference("Expenses").child(id)
        val mTask = dbRef.removeValue()

        mTask.addOnSuccessListener {
            Toast.makeText(this, "Expenses data deleted", Toast.LENGTH_LONG).show()

            val intent = Intent(this, ExpenseFetchingActivity::class.java)
            finish()
            startActivity(intent)
        }.addOnFailureListener{ error ->
            Toast.makeText(this, "Deleting Err ${error.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun openUpdateDialog(
        empId: String,
    ) {
        val mDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.update_expense, null)

        mDialog.setView(mDialogView)

        val etamount = mDialogView.findViewById<EditText>(R.id.etamount)
        val ettype = mDialogView.findViewById<EditText>(R.id.ettype)
        val etnote = mDialogView.findViewById<EditText>(R.id.etnote)

        val btnUpdateData = mDialogView.findViewById<Button>(R.id.btnUpdateData)

        etamount.setText(intent.getStringExtra("examount").toString())
        ettype.setText(intent.getStringExtra("extype").toString())
        etnote.setText(intent.getStringExtra("exnote").toString())

        mDialog.setTitle("Updating $empId Record")

        val alertDialog = mDialog.create()
        alertDialog.show()

        btnUpdateData.setOnClickListener {
            updateEmpData(
                empId,
                etamount.text.toString(),
                ettype.text.toString(),
                etnote.text.toString()
            )

            Toast.makeText(applicationContext, "Expenses Data Updated", Toast.LENGTH_LONG).show()

            //we are setting updated data to our textviews
            tvamount.text = etamount.text.toString()
            tvtype.text = ettype.text.toString()
            tvnote.text = etnote.text.toString()

            alertDialog.dismiss()
        }
    }

    private fun updateEmpData(
        expensesId : String,
        expensesAmount : String,
        expensesNote : String,
        expensesType : String
    ) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Expenses").child(expensesId)
        val empInfo = expensesModel(expensesId, expensesAmount, expensesNote, expensesType)
        dbRef.setValue(empInfo)
    }
}