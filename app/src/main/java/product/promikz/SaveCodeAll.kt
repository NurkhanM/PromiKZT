package product.promikz

import kotlin.math.roundToInt

class SaveCodeAll {


    // проверка на существование и удаление элемента из массива
    /*
        private fun isContains(array: ArrayList<Int>, value: Int): Boolean {
        return array.contains(value)
    }

    private fun removeItem(array: ArrayList<Int>, value: Int): ArrayList<Int> {
        val arr = array.filter { it != value }.toIntArray()
        return arr.toCollection(ArrayList())
    }
     */


    //OnbackPressed Fragment
//    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
//        override fun handleOnBackPressed() {
//            //
//        }
//    })



    // intent po ssylke
    //    @Suppress("DEPRECATION")
//    private fun inToEmail() {
//        val address = Uri.parse("https://www.google.com/intl/ru/gmail/about/")
//        val openlink = Intent(Intent.ACTION_VIEW, address)
//        try {
//            startActivity(openlink)
//            activity?.onBackPressed()
//        } catch (e: ActivityNotFoundException) {
//            Toast.makeText(requireContext(), resources.getText(R.string.no_select), Toast.LENGTH_SHORT).show()
//            // Define what your app should do if no activity can handle the intent.
//        }
//
//    }

    //Проверка на подлиность Int
//    private fun isFloatToInt(str: String): Int{
//        return if (str.toDoubleOrNull() == null) {
//            // код, который будет выполнен, если value не является числом с плавающей точкой
//            str.toInt()
//        } else {
//            // код, который будет выполнен, если value является числом с плавающей точкой
//            val doubleValue = str.toDouble()
//            doubleValue.roundToInt()
//        }
//    }

    // Проверка при записывании данных в телефон
//    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
//    == PackageManager.PERMISSION_GRANTED) {
//        // Разрешение на чтение уже предоставлено, обрабатываем файлы
//    } else {
//        // Запрашиваем разрешение на чтение у пользователя
//        ActivityCompat.requestPermissions(this,
//            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
//    }




}