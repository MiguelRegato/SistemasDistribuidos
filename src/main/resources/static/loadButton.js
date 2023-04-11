// Define our button click listener
$(window).on("load", function(){
	// Loading Buttons from menu
	valueIndex(1);
	$('#btnRecommendation').on("click",()=>functionRecommendation("btnRecommendation", '#moreRecommendations', '#loaderRecommendation'))
    $('#btnTrending').on("click",()=>functionCall("btnTrending", '#moreFilmsTrending', '#loaderTrending', null))
    $('#btnAction').on("click",()=>functionCall("btnAction", '#moreFilmsAction', '#loaderAction', "ACTION"))
    $('#btnAdventure').on("click",()=>functionCall("btnAdventure", '#moreFilmsAdventure', '#loaderAdventure', "ADVENTURE"))
    $('#btnAnimation').on("click",()=>functionCall("btnAnimation", '#moreFilmsAnimation', '#loaderAnimation', "ANIMATION"))
    $('#btnComedy').on("click",()=>functionCall("btnComedy", '#moreFilmsComedy', '#loaderComedy', "COMEDY"))
    $('#btnDrama').on("click",()=>functionCall("btnDrama", '#moreFilmsDrama', '#loaderDrama', "DRAMA"))
    $('#btnHorror').on("click",()=>functionCall("btnHorror", '#moreFilmsHorror', '#loaderHorror', "HORROR"))
    $('#btnScifi').on("click",()=>functionCall("btnScifi", '#moreFilmsScifi', '#loaderScifi', "SCIENCE_FICTION"))

	// Loading Button from film info
	$('#btnComment').on("click",()=>functionComment('#moreComments', '#loaderComment'))

	// Loading Button from profile
	$('#btnCommentProfile').on("click",()=>functionCommentProfile('#moreCommentsProfile', '#loaderCommentProfile'))
	$('#btnCommentWatchProfile').on("click",()=>functionCommentWatchProfile('#moreCommentsWatchProfile', '#loaderCommentWatchProfile'))

	// Loading Buttons from search film
    $('#btnSearchUnregistered').on("click",()=>functionSearch("btnSearchUnregistered", '#moreFilmsUnregistered', '#loaderSearchUnregistered'))
    $('#btnSearchRegistered').on("click",()=>functionSearch("btnSearchRegistered", '#moreFilmsRegistered', '#loaderSearchRegistered'))
    $('#btnSearchAdmin').on("click",()=>functionSearch("btnSearchAdmin", '#moreFilmsAdmin', '#loaderSearchAdmin'))

	// Loading Button from following/followers
	$('#btnFollowers').on("click",()=>functionFollowers('#moreFollowers', '#loaderFollowers'))
	$('#btnFollowing').on("click",()=>functionFollowing('#moreFollowing', '#loaderFollowing'))

})

var indexRecommendation;
var indexTrending;
var indexAction;
var indexAdventure;
var indexAnimation;
var indexComedy;
var indexDrama;
var indexHorror;
var indexScifi;

var indexComment;
var indexCommentProfile;
var indexCommentWatchProfile;

var indexSearchUnregis;
var indexSearchRegis;
var indexSearchAdmin;

var indexFollowers;
var indexFollowing;

function ajaxCall(url, spinner, where) {
	$.ajax({
    	type: "GET",
        contenType: "aplication/json",
		url: url,
		beforeSend: function () {
        	$(spinner).removeClass('hidden')
        },
		success: function (result) {
			$(where).append(result);
		},
		complete: function () {
        	$(spinner).addClass('hidden')
        },
	});
}

function functionRecommendation(index, where, spinner) {
	value = searchIndex(index);
	url=('/moreRecommendations/' + value);
	ajaxCall(url, spinner, where);
}

function functionCall(index, where, spinner, genre) {
	value = searchIndex(index);
	 
	if (genre==null) {
		url=('/more/' + value);
	} else {
		url=('/moreGenre/'+ genre + '/' + value);
	}
	
	ajaxCall(url, spinner, where);
}

function functionComment(where, spinner) {
	value = indexComment;
	this.indexComment += 1;	
	
	// Search parameter in url
	const arrayPath = window.location.pathname.split('/');
	const id= arrayPath[2];

	url=('/moreComments/' + id + '/' + value);
	
    ajaxCall(url, spinner, where);
}

function functionCommentProfile(where, spinner) {
	value = indexCommentProfile;
	this.indexCommentProfile += 1;	
	
	// Search parameter in url
	const arrayPath = window.location.pathname.split('/');
	const id= arrayPath[2];

	url=('/moreCommentsProfile/' + id + '/' + value);
	
    ajaxCall(url, spinner, where);
}

function functionCommentWatchProfile(where, spinner) {
	value = indexCommentWatchProfile;
	this.indexCommentWatchProfile += 1;	
	
	// Search parameter in url
	const arrayPath = window.location.pathname.split('/');
	const id= arrayPath[2];

	url=('/moreCommentsWatchProfile/' + id + '/' + value);
	
    ajaxCall(url, spinner, where);
}

function functionSearch(index, where, spinner) {
	value = searchIndex(index);
	 
	const queryString = window.location.search;
	const urlParams = new URLSearchParams(queryString);
	const query = urlParams.get('query')

	url=('/moreSearch/' + query + '/'+ value);

	
    ajaxCall(url, spinner, where);
}

function functionFollowers(where, spinner) {
	value = indexFollowers;
	this.indexFollowers += 1;
	
	// Search parameter in url
	const arrayPath = window.location.pathname.split('/');
	const id= arrayPath[2];
	
	url=('/moreFollowers/' + id + '/' + value);	
	ajaxCall(url, spinner, where);

}

function functionFollowing(where, spinner) {
	value = indexFollowing;
	this.indexFollowing += 1;
	
	// Search parameter in url
	const arrayPath = window.location.pathname.split('/');
	const id= arrayPath[2];
	
	url=('/moreFollowing/' + id + '/' + value);	
	ajaxCall(url, spinner, where);

}

function searchIndex(index) {
	value = 0;
	
	switch(index) {
		case ("btnRecommendation"): 
			value = indexRecommendation;
			this.indexRecommendation += 1;
			break;
		case ("btnTrending"): 
			value = indexTrending;
			this.indexTrending += 1;
			break;
		case ("btnAction"): 
			value = indexAction;
			this.indexAction += 1;
			break;
		case ("btnAdventure"): 
			value = indexAdventure;
			this.indexAdventure += 1;
			break;
		case ("btnAnimation"): 
			value = indexAnimation;
			this.indexAnimation += 1;
			break;
		case ("btnComedy"): 
			value = indexComedy;
			this.indexComedy += 1;
			break;
		case ("btnDrama"): 
			value = indexDrama;
			this.indexDrama += 1;	
			break;	
		case ("btnHorror"): 
			value = indexHorror;
			this.indexHorror += 1;
			break;
		case ("btnScifi"): 
			value = indexScifi;
			this.indexScifi += 1;	
			break;
		case ("btnSearchUnregistered"): 
			value = indexSearchUnregis;
			this.indexSearchUnregis += 1;	
			break;
		case ("btnSearchRegistered"): 
			value = indexSearchRegis;
			this.indexSearchRegis += 1;	
			break;
		case ("btnSearchAdmin"): 
			value = indexSearchAdmin;
			this.indexSearchAdmint += 1;	
			break;
	}
	
	return value;
}

function valueIndex(num) {
	this.indexRecommendation = num;
	this.indexTrending = num;
	this.indexAction = num;
	this.indexAdventure = num;
	this.indexAnimationn = num;
	this.indexComedy = num;
	this.indexDrama = num;
	this.indexHorror = num;
	this.indexScifi = num;
	
	this.indexComment = num;
	
	this.indexCommentProfile = num;
	this.indexCommentWatchProfile = num;
	
	this.indexSearchUnregis = num;
    this.indexSearchRegis = num;
    this.indexSearchAdmin = num;
    
    this.indexFollowers = num;
	this.indexFollowing = num;
}