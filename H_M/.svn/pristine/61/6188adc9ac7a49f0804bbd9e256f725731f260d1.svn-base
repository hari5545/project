$(document).ready(function(){
    console.log("Console!");

    validateFormFlag();

    $("#submitBtn").click(function(e) {
    	e.preventDefault();
    	validateLoginForm();
    });    

});

function validateFormFlag() {
	var formFlag = $("#formFlag").val();
	//console.log(formFlag);
	if(formFlag == "err-usr" || formFlag == "err-pwd" || formFlag == "unauth") {
		$("#submitBtn").notify("Invalid Credentials",{ position:"right" });
	}
	if(formFlag == "logout") {
		$("#submitBtn").notify("Logout Successful!",{ className: "success", position:"right" });
	}
}

function validateLoginForm() {
	
	var usrn = $("#username");
	var pswd = $("#password");
	var errCnt = 0;

	if(usrn.val() == "") {
		errCnt++;
		usrn.notify("Please enter the username",{ position:"right" });
	}
	else if(pswd.val() == "") {
		errCnt++;
		pswd.notify("Please enter the password",{ position:"right" });
	}

	if(errCnt == 0) {
		$("#formFlag").val("");
		$("#adminLogin").submit();
	}

}