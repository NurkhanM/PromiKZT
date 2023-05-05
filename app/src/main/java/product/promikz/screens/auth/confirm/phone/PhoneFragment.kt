package product.promikz.screens.auth.confirm.phone


import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import product.promikz.AppConstants
import product.promikz.MyUtils.uToast
import product.promikz.databinding.FragmentPhoneBinding
import product.promikz.screens.auth.confirm.PrefUtil
import product.promikz.viewModels.HomeViewModel
import java.util.*

class PhoneFragment : Fragment() {

    lateinit var viewModels: HomeViewModel
    private var _binding: FragmentPhoneBinding? = null
    private val binding get() = _binding!!

    companion object {
        val nowSeconds: Long
            get() = Calendar.getInstance().timeInMillis / 1000
    }


    enum class TimerState{
        Stopped, Paused, Running
    }

    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds: Long = 0
    private var timerState = TimerState.Stopped

    private var secondsRemaining: Long = 0

    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModels = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentPhoneBinding.inflate(inflater, container, false)



        binding.sendCodeNext.setOnClickListener{
            viewModels.sendPhone("Bearer ${AppConstants.TOKEN_USER}")
            binding.sendCodeNext.visibility = View.GONE
            binding.currentCodeText.visibility = View.VISIBLE
            startTimer()
            timerState =  TimerState.Running
        }



        binding.sBackCard.setOnClickListener {
            activity?.onBackPressed()
        }

        viewModels.mySendPhone.observe(viewLifecycleOwner){list ->
            if (list.isSuccessful){
                uToast(requireContext(), list.body()?.data?.message)
            }else {
                uToast(requireContext(), "Error Phone Confirm")
            }
        }

        viewModels.mySendCheckPhone.observe(viewLifecycleOwner){list ->
            if (list.isSuccessful){
                uToast(requireContext(), list.body()?.data?.message)
                activity?.onBackPressed()
            }else {
                binding.progress.visibility = View.GONE
                binding.btnConfirm.visibility = View.VISIBLE
                uToast(requireContext(), "Неправильный код")
            }
        }

        binding.btnConfirm.setOnClickListener {
            if (binding.authEditCode.text?.isNotEmpty() == true){
                binding.progress.visibility = View.VISIBLE
                binding.btnConfirm.visibility = View.GONE
                viewModels.sendCheckPhone("Bearer ${AppConstants.TOKEN_USER}", binding.authEditCode.text.toString())
            }else {
                uToast(requireContext(), "Пустое поле")
            }
        }


        return binding.root
    }


    private fun startTimer(){
        timerState = TimerState.Running

        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
            }
        }.start()
    }

    private fun onTimerFinished(){
        timerState = TimerState.Stopped

        setNewTimerLength()
        PrefUtil.setSecondsRemaining(timerLengthSeconds, requireContext())
        secondsRemaining = timerLengthSeconds

        updateCountdownUI()

        binding?.sendCodeNext?.visibility = View.VISIBLE
        binding?.currentCodeText?.visibility = View.GONE
        uToast(requireContext(), "finish")
    }

    private fun setNewTimerLength(){
        val lengthInMinutes = PrefUtil.getTimerLength(requireContext())
        timerLengthSeconds = (lengthInMinutes * 60L)
    }

    private fun setPreviousTimerLength(){
        timerLengthSeconds = PrefUtil.getPreviousTimerLengthSeconds(requireContext())
    }

    @SuppressLint("SetTextI18n")
    private fun updateCountdownUI(){
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinuteUntilFinished = secondsRemaining - minutesUntilFinished * 60
        val secondsStr = secondsInMinuteUntilFinished.toString()
        binding?.currentCodeText?.text = "$minutesUntilFinished:${if (secondsStr.length == 2) secondsStr else "0 $secondsStr"}"
    }

    override fun onResume() {
        super.onResume()

        initTimer()
    }

    private fun initTimer(){
        timerState = PrefUtil.getTimerStatePhone(requireContext())

        if (timerState == TimerState.Stopped)
            setNewTimerLength()
        else
            setPreviousTimerLength()

        secondsRemaining = if (timerState == TimerState.Running || timerState == TimerState.Paused){
            PrefUtil.getSecondsRemaining(requireContext())
        } else {
            timerLengthSeconds
        }


        val alarmSetTime = PrefUtil.getAlarmSetTime(requireContext())
        if (alarmSetTime > 0){
            secondsRemaining -= nowSeconds - alarmSetTime
        }


        if (secondsRemaining <= 0)
            onTimerFinished()
        else if (timerState == TimerState.Running){
            startTimer()
            binding?.sendCodeNext?.visibility = View.GONE
            binding?.currentCodeText?.visibility = View.VISIBLE
        }


        updateCountdownUI()
    }

    override fun onPause() {
        super.onPause()

            if (timerState == TimerState.Running){
                timer.cancel()
            }

            PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, requireContext())
            PrefUtil.setSecondsRemaining(secondsRemaining, requireContext())
            PrefUtil.setTimerStatePhone(timerState, requireContext())

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}