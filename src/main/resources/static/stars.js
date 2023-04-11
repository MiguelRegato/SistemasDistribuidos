
$(function () {
  $(".rateyo").rateYo().on("rateyo.change", function (data) {
	
    var rating = data.rating;
    $(this).parent().find('.result').text('rating :'+ rating);
   });
});
