package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import java.util.Stack


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var res = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calculator)

        val buttons = intArrayOf(
            R.id.ffirst,  R.id.fsecond,R.id.fthird,R.id.ffourth,
            R.id.sfirst, R.id.ssecond, R.id.sthird,R.id.sfourth,
            R.id.tfirst, R.id.tsecond, R.id.tthird,R.id.tfourth,
            R.id.fofirst, R.id.fosecond, R.id.fothird,R.id.fofourth,
            R.id.fisecond, R.id.fithird, R.id.fifourth
        )

        for (id in buttons) {
            findViewById<AppCompatButton>(id).setOnClickListener(this)
        }
    }
    override fun onClick(view: View) {
        when (view.id) {
            R.id.ffirst -> res=""
            R.id.fsecond -> res += if(res=="" || isOp(res[res.length-1])){
                                "("
                            }else{
                                ")"
                            }
            R.id.fthird -> res+="%"
            R.id.ffourth -> if(isOp(res[res.length-1])) {
                                var str=""
                                str=res.replace(res[res.length-1],'/',ignoreCase = false)
                                res=str
                             } else res+='/'
            R.id.sfirst ->  res += "7"
            R.id.ssecond -> res += "8"
            R.id.sthird -> res += "9"
            R.id.sfourth -> if(isOp(res[res.length-1])){
                                var str=""
                                str= res.replace(res[res.length-1],'*',ignoreCase = false)
                                res=str
                            } else res+='*'
            R.id.tfirst -> res += "4"
            R.id.tsecond -> res += "5"
            R.id.tthird -> res += "6"
            R.id.tfourth -> if(res!="" && isOp(res[res.length-1])){
                                var str=""
                                str= res.replace(res[res.length-1],'-',ignoreCase = false)
                                res=str
                            } else res+='-'
            R.id.fofirst -> res += "1"
            R.id.fosecond -> res += "2"
            R.id.fothird -> res += "3"
            R.id.fofourth -> if(isOp(res[res.length-1])){
                                var str=""
                                str= res.replace(res[res.length-1],'+',ignoreCase = false)
                                res=str
                             } else res+='+'
            R.id.fisecond -> res += "0"
            R.id.fithird -> res += "."
            R.id.fifourth -> calculateResult()
        }
        findViewById<EditText>(R.id.edittext).setText(res)
        adjustTextSize()
    }
    private fun adjustTextSize() {
        val editText = findViewById<EditText>(R.id.edittext)
        val length = editText.text.length

        when {
            length > 13 -> editText.textSize = 24f
            else -> editText.textSize = 30f
        }
    }

    private fun isOp(c : Char ):Boolean{
        return (c=='+'|| c=='-'|| c=='*'|| c=='/'|| c=='%')
    }
    private fun hasPrecedence(op1:Char, op2:Char):Boolean{
        return !((op1 == '*' || op1 == '/'|| op1=='%') && (op2 == '+' || op2 == '-'))
        // return (op1=='+' || op1=='-') && (op2=='*' || op2=='/')
    }
    private fun isDigit(c : Char) :Boolean{
        return c in '0'..'9' || c == '.'
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    private fun applyOp(op:Char, b: Double, a:Double):Double {
        return when(op){
            '+'-> a+b
            '-'-> a-b
            '*'-> a*b
            '/' ->
            {
                if (b == 0.0) {
                    Toast.makeText(this, "Can't divide by zero.", Toast.LENGTH_SHORT).show()
                    //showToast("Can't divide by zero.")
                    Double.NaN
                } else {
                    a / b
                }
            }
            '%' -> {
                if (b == 0.0) {
                    Toast.makeText(this, "Can't perform modulo by zero.", Toast.LENGTH_SHORT).show()
                    //showToast("Can't perform modulo by zero.")
                    Double.NaN
                } else {
                    a % b
                }
            }
            else-> 0.0
        }
    }
    private fun checkValidExpression(str : String):Boolean{
        return !((res!="" && (res[0]=='+' || res[0]=='*' || res[0]=='/')) || (res[res.length-1]=='+' || res[res.length-1]=='*' || res[res.length-1]=='/'))
    }
    private fun calculateResult() {
        if(!checkValidExpression(res)){
            Toast.makeText(this,"Invalid format used.",Toast.LENGTH_LONG).show()
            return
        }
        val tokens: CharArray = res.toCharArray()

        val values = Stack<Double>()
        val ops = Stack<Char>()

        var i = 0
        while(i<tokens.size){
            if(isDigit(tokens[i])){
                val str=StringBuilder()

                while(i< tokens.size && (isDigit(tokens[i]) || tokens[i]=='.')){
                    str.append(tokens[i++])
                }
                values.push(str.toString().toDouble())
            }
            else if(tokens[i]=='('){
                ops.push('(')
                i++
            }else if(tokens[i]==')'){
                while(ops.peek() != ')'){
                    values.push(applyOp(ops.pop(),values.pop(),values.pop()))
                }
                ops.pop()
                i++
            }else if(tokens[i]=='%' || tokens[i] =='+' || tokens[i] =='-' || tokens[i] =='*' || tokens[i] =='/'){
                if(tokens[i]=='-' && (i==0 || tokens[i-1]=='(')){
                    val str=StringBuilder()
                    str.append('-')
                    i++
                    while(i< tokens.size && (isDigit(tokens[i]) || tokens[i]=='.')){
                        str.append(tokens[i++])
                    }
                    values.push(str.toString().toDouble())
                }else{
                    while(ops.isNotEmpty() && hasPrecedence(tokens[i],ops.peek())){

                        val result = applyOp(ops.pop(), values.pop(), values.pop())
                        if (!result.isNaN()) values.push(result)
                        else return
                        //values.push(applyOp(ops.pop(),values.pop(),values.pop()))
                    }
                    ops.push(tokens[i])
                    i++
                }
            }
        }
        while(ops.isNotEmpty()){

            val result = applyOp(ops.pop(), values.pop(), values.pop())
            if (!result.isNaN()) values.push(result)
            else return
            //values.push(applyOp(ops.pop(),values.pop(),values.pop()))
        }
        res= values.pop().toString()
        findViewById<EditText>(R.id.edittext).setText(res)
    }
}
