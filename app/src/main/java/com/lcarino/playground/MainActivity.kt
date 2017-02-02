package com.lcarino.playground

import android.os.Bundle
import android.support.constraint.ConstraintSet
import android.support.constraint.solver.widgets.ConstraintWidget
import android.support.v7.app.AppCompatActivity
import android.transition.TransitionManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var animationNumber = 0
    internal var applyConstrainSet: ConstraintSet = ConstraintSet()
    internal var resetConstrainSet: ConstraintSet = ConstraintSet()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonApply.setOnClickListener { v -> executeAnimation() }
        buttonReset.setOnClickListener { v -> reset() }

        applyConstrainSet.clone(myConstraintLayout)
        resetConstrainSet.clone(myConstraintLayout)

        val spinnerOptions = arrayOf("constraint start",
                "smooth constraint start",
                "align center",
                "3 to center",
                "expand width",
                "rect reveal",
                "chained packed",
                "chain spread",
                "chain spread inside")
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerOptions)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        mySpinner.adapter = arrayAdapter
        mySpinner.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        animationNumber = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // no-op
    }

    fun executeAnimation() {
        when (animationNumber) {
            0 -> animate()
            1 -> animateWithTransitionManger()
            2 -> centerHorizontally()
            3 -> buttonThreeToCenter()
            4 -> expandWidth()
            5 -> rectReveal()
            6 -> chainedConstraint(ConstraintWidget.CHAIN_PACKED)
            7 -> chainedConstraint(ConstraintWidget.CHAIN_SPREAD)
            8 -> chainedConstraint(ConstraintWidget.CHAIN_SPREAD_INSIDE)
            else -> animate()
        }
    }

    private fun chainedConstraint(chainStyle: Int) {
        TransitionManager.beginDelayedTransition(myConstraintLayout)

        applyConstrainSet.clear(R.id.button1)
        applyConstrainSet.clear(R.id.button2)
        applyConstrainSet.clear(R.id.button3)

        applyConstrainSet.constrainWidth(R.id.button1, ConstraintSet.WRAP_CONTENT)
        applyConstrainSet.constrainHeight(R.id.button1, ConstraintSet.WRAP_CONTENT)
        applyConstrainSet.constrainWidth(R.id.button2, ConstraintSet.WRAP_CONTENT)
        applyConstrainSet.constrainHeight(R.id.button2, ConstraintSet.WRAP_CONTENT)
        applyConstrainSet.constrainWidth(R.id.button3, ConstraintSet.WRAP_CONTENT)
        applyConstrainSet.constrainHeight(R.id.button3, ConstraintSet.WRAP_CONTENT)


        applyConstrainSet.connect(R.id.button1, ConstraintSet.LEFT, R.id.myConstraintLayout, ConstraintSet.LEFT, 0)
        applyConstrainSet.connect(R.id.button3, ConstraintSet.RIGHT, R.id.myConstraintLayout, ConstraintSet.RIGHT, 0)

        // apply bi-directional chain
        applyConstrainSet.connect(R.id.button2, ConstraintSet.LEFT, R.id.button1, ConstraintSet.RIGHT, 0)
        applyConstrainSet.connect(R.id.button1, ConstraintSet.RIGHT, R.id.button2, ConstraintSet.LEFT, 0)

        applyConstrainSet.connect(R.id.button2, ConstraintSet.RIGHT, R.id.button3, ConstraintSet.LEFT, 0)
        applyConstrainSet.connect(R.id.button3, ConstraintSet.LEFT, R.id.button2, ConstraintSet.RIGHT, 0)


        /**
         * This method crate’s a Horizontal chain for us. So this method will take 5 arguments.
        First: Id of a head view.
        Second: Id of a tail view in chain.
        Third: int array, give head and tail view ids as int array.
        Fourth: float array, give weight if we want weight chaining otherwise null.
        Fifth: Style of chaining like CHAIN_SPREAD.
         */
        applyConstrainSet.createHorizontalChain(R.id.button1,
                R.id.button3,
                intArrayOf(R.id.button1, R.id.button3),
                null,
                chainStyle)

        applyConstrainSet.applyTo(myConstraintLayout)

    }

    private fun rectReveal() {
        TransitionManager.beginDelayedTransition(myConstraintLayout)
        applyConstrainSet.setVisibility(R.id.button2, ConstraintSet.GONE)
        applyConstrainSet.setVisibility(R.id.button3, ConstraintSet.GONE)
        // remove constraints
        applyConstrainSet.clear(R.id.button1)
        // apply constraint to cover the entire screen
        applyConstrainSet.connect(R.id.button1, ConstraintSet.LEFT, R.id.myConstraintLayout, ConstraintSet.LEFT, 0)
        applyConstrainSet.connect(R.id.button1, ConstraintSet.RIGHT, R.id.myConstraintLayout, ConstraintSet.RIGHT, 0)
        applyConstrainSet.connect(R.id.button1, ConstraintSet.TOP, R.id.myConstraintLayout, ConstraintSet.TOP, 0)
        applyConstrainSet.connect(R.id.button1, ConstraintSet.BOTTOM, R.id.myConstraintLayout, ConstraintSet.BOTTOM, 0)

        applyConstrainSet.applyTo(myConstraintLayout)
    }

    private fun expandWidth() {
        TransitionManager.beginDelayedTransition(myConstraintLayout)
        applyConstrainSet.constrainWidth(R.id.button1, 600)
        applyConstrainSet.constrainWidth(R.id.button2, 600)
        applyConstrainSet.constrainWidth(R.id.button3, 600)
        applyConstrainSet.applyTo(myConstraintLayout)
    }

    fun reset() {
        TransitionManager.beginDelayedTransition(myConstraintLayout)
        resetConstrainSet.applyTo(myConstraintLayout)
    }

    fun animate() {
        applyConstrainSet.setMargin(R.id.button1, ConstraintSet.START, 0)
        applyConstrainSet.applyTo(myConstraintLayout)
    }

    fun animateWithTransitionManger() {
        TransitionManager.beginDelayedTransition(myConstraintLayout)
        applyConstrainSet.setMargin(R.id.button1, ConstraintSet.START, 0)
        applyConstrainSet.applyTo(myConstraintLayout)
    }

    fun centerHorizontally() {
        TransitionManager.beginDelayedTransition(myConstraintLayout)
        // remove margins before center to fix issue ??
        removeHorizontalMargins(R.id.button1)
        removeHorizontalMargins(R.id.button2)
        removeHorizontalMargins(R.id.button3)
        // apply new constraints
        applyConstrainSet.centerHorizontally(R.id.button1, R.id.myConstraintLayout)
        applyConstrainSet.centerHorizontally(R.id.button2, R.id.myConstraintLayout)
        applyConstrainSet.centerHorizontally(R.id.button3, R.id.myConstraintLayout)
        applyConstrainSet.applyTo(myConstraintLayout)
    }

    fun buttonThreeToCenter() {
        TransitionManager.beginDelayedTransition(myConstraintLayout)
        removeHorizontalMargins(R.id.button3)
        removeVerticalMargins(R.id.button3)

        applyConstrainSet.centerHorizontally(R.id.button3, R.id.myConstraintLayout)
        applyConstrainSet.centerVertically(R.id.button3, R.id.myConstraintLayout)
        applyConstrainSet.applyTo(myConstraintLayout)
    }

    fun removeHorizontalMargins(id : Int) {
        applyConstrainSet.setMargin(id, ConstraintSet.START, 0)
        applyConstrainSet.setMargin(id, ConstraintSet.END, 0)
    }

    fun removeVerticalMargins(id : Int) {
        applyConstrainSet.setMargin(id, ConstraintSet.TOP, 0)
        applyConstrainSet.setMargin(id, ConstraintSet.BOTTOM, 0)
    }
}
