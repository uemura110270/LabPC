
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() , View.OnClickListener{

    // タップ数を格納しておくための変数
    var count: Int = 0
    // TextViewのインスタンスを格納する変数
    var countText: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // idからTextViewのオブジェクトを取得
        countText = this.findViewById(R.id.count)
        countText!!.text = count.toString()

        // idからButtonのオブジェクトを取得
        val btn: Button = this.findViewById(R.id.btn)
        // ボタンがタップを検知するように
        btn.setOnClickListener{
            count += 1
            countText!!.text = count.toString()
        }
    }

}