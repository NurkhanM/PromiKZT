package product.promikz.screens.sravnit

import product.promikz.models.compare.Data

class ProductComparator : Comparator<Data> {
    override fun compare(d1: Data, d2: Data): Int {
        val imgComparison = d1.img.compareTo(d2.img)
        if (imgComparison != 0) {
            return imgComparison
        }
        val nameComparison = d1.name.compareTo(d2.name)
        if (nameComparison != 0) {
            return nameComparison
        }
        val categoriesComparison = d1.categories[0].name.compareTo(d2.categories[0].name)
        if (categoriesComparison != 0) {
            return categoriesComparison
        }
        val brandComparison = d1.brand.name.compareTo(d2.brand.name)
        if (brandComparison != 0) {
            return brandComparison
        }
        val priceComparison = d1.price.compareTo(d2.price)
        if (priceComparison != 0) {
            return priceComparison
        }
//        val likeCountComparison = d1.likeCount.compareTo(d2.likeCount)
//        if (likeCountComparison != 0) {
//            return likeCountComparison
//        }
//        val reviewCountComparison = d1.reviewCount.compareTo(d2.reviewCount)
//        if (reviewCountComparison != 0) {
//            return reviewCountComparison
//        }

        return 0
    }
}
