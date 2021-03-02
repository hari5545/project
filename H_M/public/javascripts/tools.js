$(document).ready(function(){
	window.isRoleToggleInitialized = false;
	window.isTabToggleInitialized = false;

	window.userData;
	// to disable input in survey module
	$('#input-options').hide();


	console.log("Homing Admin!");

	$.notify.addStyle('multi-line-text', {
		html: "<div><span data-notify-text></span></div>",
		classes: {
			base: {
				"font-weight": "bold",
				"padding": "8px 15px 8px 14px",
				"text-shadow":" 0 1px 0 rgba(255, 255, 255, 0.5)",
				"background-color":"#fcf8e3",
				"border": "1px solid #fbeed5",
				"-webkit-border-radius": "4px",
				"-moz-border-radius": "4px",
				"border-radius":"4px",
				"white-space":"normal",
				"padding-left":"25px",
				"background-repeat":"no-repeat",
				"background-position":"3px 7px",
				"color": "#B94A48",
				"background-color": "#F2DEDE",
				"border-color": "#EED3D7",
				"width": "30em",
				"background-image": "url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAAAGXRFW…jm+8nm4IuE/9u+/PH2JXZfbwz4zw1WbO+SQPpXfwG/BBgAhCNZiSb/pOQAAAAASUVORK5CYII=)"
			}
		}
	});
	$(".modal-content-inside").hide();

	$('#create-btn').click(function(e) {
		var data_source = $(this).attr("data-current-content");
		console.log(data_source);
		if(data_source == "create-organization") {
			validateCreateOrganization();
		}
		if(data_source == "create-organization-unit") {
			validateCreateOrganizationUnit();
		}
		if(data_source == "create-department") {
			validateCreateDepartment();
		}
		if(data_source == "create-role") {
			validateCreateRole();
		}
		if(data_source == "create-user") {
			validateCreateUser();
		}
		if(data_source == "create-tab") {
			validateCreateTab();
		}

	});

	$('#create-news-btn').click(function(e) {
		validateCreateNews()
	});

	$('#create-survey-btn').click(function(e) {
		validateSurvey();
	});


	$('#createModal').on('show.bs.modal', function (event) {
		$(".modal-content-inside").hide();
		var button = $(event.relatedTarget); // Button that triggered the modal
		var modalTitle = button.data('modal-title'); // Extract info from data-* attributes
		var modalContent = button.data('content'); // Extract info from data-* attributes	  
		var modal = $(this);
		modal.find('.modal-title').text(modalTitle);
		$("#"+modalContent).show();
		$('#create-btn').attr("data-current-content", modalContent);
		updateOrganizationList(modalContent);
	});

	// for news 

	$('#newsModal').on('show.bs.modal', function (event) {
		$(".modal-content-inside").hide();
		var button = $(event.relatedTarget); // Button that triggered the modal
		var modalTitle = button.data('modal-title'); // Extract info from data-* attributes
		var modalContent = button.data('content'); // Extract info from data-* attributes	  
		var modal = $(this);
		modal.find('.modal-title').text(modalTitle);
		$("#"+modalContent).show();
		$('#create-news-btn').attr("data-current-content", modalContent);
		updateOrganizationList(modalContent);
	});

	// for survey

	$('#createSurveyModal').on('show.bs.modal', function (event) {
		$(".modal-content-inside").hide();
		var button = $(event.relatedTarget); // Button that triggered the modal
		var modalTitle = button.data('modal-title'); // Extract info from data-* attributes
		var modalContent = button.data('content'); // Extract info from data-* attributes	  
		var modal = $(this);
		modal.find('.modal-title').text(modalTitle);
		$("#"+modalContent).show();
		$('#create-survey-btn').attr("data-current-content", modalContent);
		updateOrganizationList(modalContent);
	});


	$('#viewModal').on('show.bs.modal', function (event) {
		$("#view-modal-body").html("");
		var button = $(event.relatedTarget); // Button that triggered the modal
		var modalTitle = button.data('modal-title'); // Extract info from data-* attributes
		var modalContent = button.data('content');
		var modal = $(this);
		modal.find('.modal-title').text(modalTitle);
		getList(modalContent);
	});


	$('#viewXlModal').on('show.bs.modal', function (event) {
		$("#view-xl-modal-body").html("");
		var button = $(event.relatedTarget); // Button that triggered the modal
		var modalTitle = button.data('modal-title'); // Extract info from data-* attributes
		var modalContent = button.data('content');
		var modal = $(this);
		modal.find('.modal-title').text(modalTitle);
		getList(modalContent);
	});

	/*$('#assignRole4UserModal').on('show.bs.modal', function (event) {
			var button = $(event.relatedTarget);
			var modalContent = button.data('content');
			//console.log(modalContent);
			updateOrganizationList(modalContent);
	    	initializeAssignRolesUser();
		});*/

	$('#assignRole4UserModal').on('show.bs.modal', function (event) {
		var button = $(event.relatedTarget);
		var modalContent = button.data('content');
		//console.log(modalContent);
		updateOrganizationList(modalContent);
		initializeAssignRolesUser();
		if(!isRoleToggleInitialized){
			//console.log("isRoleToggleInitialized:" + window.isRoleToggleInitialized);
			setTimeout(initializeToggleClick4Role, 1000);
		}	    	
	});

	$('#assignRole4TabModal').on('show.bs.modal', function (event) {
		var button = $(event.relatedTarget);
		var modalContent = button.data('content');
		//console.log(modalContent);
		updateOrganizationList(modalContent);
		initializeAssignRolesTab();
		//resetAssignRoles4Tab();
		if(!isTabToggleInitialized){
			//console.log("isRoleToggleInitialized:" + window.isRoleToggleInitialized);
			setTimeout(initializeToggleClick4Tab, 1000);
		}

	});

	$('#editOrgModal').on('show.bs.modal', function (event) {
		var button = $(event.relatedTarget); // Button that triggered the modal
		var orgName = button.data('content'); // Extract info from data-* attributes	  
		var modal = $(this);
		loadHisUrl(orgName);
	});
	/*$('#editSurveyModal').on('show.bs.modal', function (event) {
			$(".modal-content-inside").hide();
			var button = $(event.relatedTarget); // Button that triggered the modal
			var modalTitle = button.data('modal-title'); // Extract info from data-* attributes
			var modalContent = button.data('content'); // Extract info from data-* attributes	  
			var modal = $(this);
			modal.find('.modal-title').text(modalTitle);
			$("#"+modalContent).show();
			$('#create-survey-btn').attr("data-current-content", modalContent);
			updateOrganizationList(modalContent);
		});*/

});


function editUser(selOrg,userName) {
	var orgName = $("#"+selOrg).val();
	$.ajax({
		type: "GET",
		url: '/getUser',
		data: {
			"userName":userName,"orgName":orgName
		},
		success: function(data, textStatus, jqXHR) {
			if (data) {
				//console.log(data);
				window.userData = data;
				$("#edit-user-first-name").val(data.firstName);
				$("#edit-user-last-name").val(data.lastName);
				$("#edit-user-qualification").val(data.qualification);
				$("#edit-user-specialization").val(data.specialization);
				$("#edit-user-employee-id").val(data.employeeId);
				$("#edit-user-aadhaar").val(data.aadhaarNumber);
				$("#edit-user-pan").val(data.panNumber);
				$("#edit-user-passport").val(data.passportNumber);
				$("#edit-user-phone").val(data.phoneNumber.replace("+91",""));
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("#patch-modal-body").html("<h3>" + errorThrown + "</h3>");
		}
	});
}

function patchUser() {

	window.userData.firstName = $("#edit-user-first-name").val();
	window.userData.lastName = $("#edit-user-last-name").val();
	window.userData.qualification = $("#edit-user-qualification").val();
	window.userData.specialization = $("#edit-user-specialization").val();
	window.userData.employeeId = $("#edit-user-employee-id").val();
	window.userData.aadhaarNumber = $("#edit-user-aadhaar").val();
	window.userData.panNumber = $("#edit-user-pan").val();
	window.userData.passportNumber = $("#edit-user-passport").val();
	window.userData.phoneNumber = "+91" + $("#edit-user-phone").val();

	//console.log(JSON.stringify(window.userData));

	$.ajax({
		type: "PUT",
		url: '/updateUser',
		dataType: "json",
		contentType: "application/json; charset=utf-8",
		data: JSON.stringify(window.userData),
		success: function(data, textStatus, jqXHR) {
			if (data) {
				console.log(data);
				$.notify(data.statusString,{className: "success", position:"top center" });
				$("#editUserModal").modal('hide');
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$.notify(errorThrown,{className: "success", position:"top center" });
			$("#editUserModal").modal('hide');
		}
	});
}


function editTab(selOrg,tabName) {
	var orgName = $("#"+selOrg).val();
	$.ajax({
		type: "GET",
		url: '/getTab',
		data: {
			"tabName":tabName,"orgName":orgName
		},
		success: function(data, textStatus, jqXHR) {
			if (data) {
				//	console.log(data);
				window.tabData = data;
				$("#edit-tab-name").val(data.channelName).prop('disabled', true);
				$("#edit-tab-display-name").val(data.channelDisplayName);
				$("#edit-tab-header").val(data.header);
				$("#edit-tab-purpose").val(data.purpose);
				$("#edit-tab-template").append(new Option(data.templateName, data.templateName)).prop("disabled",true);
				$("#edit-tab-org").append(new Option(data.organizationName, data.organizationName)).prop('disabled', true);
				$("#edit-tab-org-unit").append(new Option(data.organizationUnitName, data.organizationUnitName)).prop("disabled",true);
				$("#edit-tab-department").append(new Option(data.departmentName, data.departmentName)).prop("disabled",true);
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("#patch-tab-modal-body").html("<h3>" + errorThrown + "</h3>");
		}
	});
}


function patchTab() {

	window.tabData.channelName = $("#edit-tab-name").val();
	window.tabData.channelDisplayName = $("#edit-tab-display-name").val();
	window.tabData.header =  $("#edit-tab-header").val();
	window.tabData.purpose = $("#edit-tab-purpose").val();
	window.tabData.templateName = $("#edit-tab-template").val();
	window.tabData.organizationName = $("#edit-tab-org").val();
	window.tabData.organizationUnitName = $("#edit-tab-org-unit").val();
	window.tabData.departmentName = $("#edit-tab-department").val();

	//console.log(JSON.stringify(window.tabData));

	$.ajax({
		type: "PUT",
		url: '/updateTab',
		dataType: "json",
		contentType: "application/json; charset=utf-8",
		data: JSON.stringify(window.tabData),
		success: function(data, textStatus, jqXHR) {
			if (data) {
				console.log(data);
				$.notify(data.statusString,{className: "success", position:"top center" });
				$("#editTabModal").modal('hide');
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$.notify(errorThrown,{className: "success", position:"top center" });
			$("#editTabModal").modal('hide');
		}
	});
}

function loadHisUrl(orgName) {
	$("#selectedOrg").val(orgName);
	$.ajax({
		type: "GET",
		url: '/getHisUrl',
		data: {
			'orgName': orgName
		},
		success: function(data, textStatus, jqXHR) {
			if (data) {
				//console.log(data);
				$("#changeHisURL").val(data);
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$('#changeHisURL').notify('Error',{ className: "error", position:"bottom" });
		}
	});
}

function updateHisOrg() {
	let hisURL = $("#changeHisURL").val();
	let orgName = $("#selectedOrg").val();
	$.ajax({
		type: "PUT",
		url: '/updateHisUrl',
		data: {
			'orgName': orgName, 'hisURL': hisURL
		},
		success: function(data, textStatus, jqXHR) {
			if (data) {
				console.log(data);
				$.notify(data,{ className: "success", position:"top center" });
				$("#editOrgModal").modal('hide');
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$('#changeHisURL').notify('Error',{ className: "error", position:"bottom" });
		}
	});
}

function toggleCreation() {
	var creationTypeInfo = $("input[name='org-upload-type']:checked").attr("id");
	console.log(creationTypeInfo);
	$(".creationType").show();
	$("."+creationTypeInfo).hide();
}

function initializeToggleClick4Role() {
	//console.log("initializeToggleClick4Role");
	$('.assign-roles-2-users').on('click', '.list-group .li-item', function () {
		$(this).toggleClass('active');
	});
	window.isRoleToggleInitialized = true;
}

function initializeToggleClick4Tab() {
	//console.log("initializeToggleClick4Tab");
	$('.assign-roles-2-tabs').on('click', '.list-group .li-item', function () {
		//console.log($(this));
		$(this).toggleClass('active');
	});
	window.isTabToggleInitialized = true;
}


function displaySurvey(type,questionID,typesID,optionsID){
	var question = $("#"+questionID);
	var types = $("#"+typesID);
	var options=$("#"+optionsID);
	var errCnt = 0;
	if(question.val()==""){
		question.notify("Please enter question ",{ position:"bottom" });
		errCnt++;
	}
	if(types.val()==""||types.val()=="--Select--"){
		types.notify("Please select options",{ position:"bottom" });
		errCnt++;
	}
	if(errCnt == 0){
		if(type=="save"){
			var row = $("<tr><td id='question'>"+question.val() + "</td><td id='types'>" + types.val() + "</td><td id='options'>"+options.val()+"</td></tr>");
			$("#surveyBody").append(row);
			$("#question").val("");
			$("#types").val("--Select--");
			$('#input-options').hide();
		}else{
			var row = $("<tr><td id='question'>"+
					question.val() + 
					"</td><td id='types'>" + 
					types.val() +
					"</td><td id='options'>"+
					options.val()+
			"</td><td><input type='checkBox' class='form-check-input' name='checkbox' id='checkbox' checked/></td></tr>");
			$("#editsurveyBody").append(row);
			$("#editQuestion").val("");
			$("#editTypes").val("--Select--");
			$('#edit-input-options').hide();
		}
	}

}

function validateCreateOrganization() {
	var orgname = $("#orgname");
	var supportemail = $("#supportemail");
	var supportnum = $("#supportnum");
	var hisURL = $("#hisURL");
	var excelFile = $("#excelFile");

	var creationTypeInfo = $("input[name='org-upload-type']:checked").attr("id");

	var errCnt = 0;
	let isValidOrgName = "";

	console.log(creationTypeInfo);

	if(hisURL.val() == "") {
		hisURL.notify("Please enter the HIS URL",{ position:"bottom" });
		errCnt++;
	} /*else {
		if(!isValidName(orgname.val())) { 
			orgname.notify("Please enter a valid organization name",{ position:"bottom" });
			orgname.focus();
			return false;
			errCnt++;
		}
	}*/

	if(creationTypeInfo == "manual") {
		if(orgname.val() == "") {
			orgname.notify("Please enter the organization name",{ position:"bottom" });
			errCnt++;
		} else {
			if(!isValidName(orgname.val())) { 
				orgname.notify("Please enter a valid organization name",{ position:"bottom" });
				orgname.focus();
				return false;
				errCnt++;
			}
		}
	}

	if(creationTypeInfo == "bulk") {
		if(document.getElementById("excelFile").files.length == 0) {
			excelFile.notify("Please select the file",{ position:"bottom" });
			errCnt++;
		} /*else {
			if(!isValidName(orgname.val())) { 
				orgname.notify("Please enter a valid organization name",{ position:"bottom" });
				orgname.focus();
				return false;
				errCnt++;
			}
		}*/
	}


	/*if(supportemail.val() == "") {
		supportemail.notify("Please enter the support email",{ position:"bottom" });
		errCnt++;
	} else {
		if(!isValidEmail(supportemail.val())) { 
			supportemail.notify("Please enter a valid email",{ position:"bottom" });
			supportemail.focus();
			return false;
			errCnt++;
		}
	}
	if(supportnum.val() == "") {
		supportnum.notify("Please enter the support mobile number",{ position:"bottom" });
		errCnt++;
	} else {
		if(!isValidPhone(supportnum.val())) { 
			supportnum.notify("Please enter a valid mobile number",{ position:"bottom" });
			supportnum.focus();
			return false;
			errCnt++;
		}
	}*/
	/*if(orgname.val() != "") {
		isValidOrgName = await validateOrganizationName(orgname.val());
		console.log(isValidOrgName);
		if(isValidOrgName != "true") 
			errCnt++;
	}*/
	//console.log(errCnt);		

	if(errCnt == 0) {
		console.log("validation successful");

		if(creationTypeInfo == "bulk") {
			console.log("bulk upload init");
			$.notify("Please wait",{ className: "info", position:"top center" });
			document.importOrg.submit();
		}

		$.ajax({
			type: 'GET',
			url: '/validateOrgName',
			data: {
				'orgName': orgname.val()
			},
			success: function(data, textStatus, jqXHR) {
				if (data) {
					console.log(data);
					if(data == "true") {
						//console.log("validation success");

						if(creationTypeInfo == "manual") {
							console.log("manual upload init");
							var supportnumVal = $("#supportnumPrefix").text() + supportnum.val();
							$.ajax({
								type: "GET",
								url: '/createOrganization',
								data: {
									'orgName': orgname.val(), "orgSupportEmail": supportemail.val(), "orgSupportContactNumber": supportnumVal
								},
								success: function(data, textStatus, jqXHR) {
									if (data) {
										//console.log(data);
										$.notify(data,{ className: "success", position:"top center" });
										$('#createModal').modal('toggle');
										resetForm();
									}
								},
								error: function(jqXHR, textStatus, errorThrown) {
									$('#create-btn').notify(data,{ className: "error", position:"right" });
								}
							});
						}
					}//orgname Validation
					else {
						$('#orgname').notify('Organization name is not available',{ className: "error", position:"bottom" });
					}
				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				console.log(errorThrown);
				return "false";
			}
		});


		/**/
	}
}

function validateCreateOrganizationUnit() {
	var orgunit = $("#orgunit");
	var selOrg = $("#selOrgOU");
	var orgloc = $("#orgloc");
	var orglat = $("#orglat");
	var orglong = $("#orglong");
	var orgemrno = $("#orgemrno");

	var errCnt = 0;

	if(orgunit.val() == "") {
		orgunit.notify("Please enter the organization unit name",{ position:"bottom" });
		errCnt++;
	} else {
		if(!isValidName(orgunit.val())) { 
			orgunit.notify("Please enter a valid organization unit name",{ position:"bottom" });
			orgunit.focus();
			return false;
			errCnt++;
		}
	}

	if(selOrg.val() == "") {
		selOrg.notify("Please select an organization",{ position:"bottom" });
		errCnt++;
	}

	if(orgloc.val() == "") {
		orgloc.notify("Please enter the location details",{ position:"bottom" });
		errCnt++;
	}

	/*if(orglat.val() == "") {
		orglat.notify("Please enter the latitude",{ position:"bottom" });
		errCnt++;
	}
	if(orglong.val() == "") {
		orglong.notify("Please enter the longitude",{ position:"bottom" });
		errCnt++;
	}
	if(orgemrno.val() == "") {
		orgemrno.notify("Please enter the emergency mobile number",{ position:"bottom" });
		errCnt++;
	} else {
		if(!isValidPhone(orgemrno.val())) { 
			orgemrno.notify("Please enter a valid mobile number",{ position:"bottom" });
			orgemrno.focus();
			return false;
			errCnt++;
		}
	}*/

	if(errCnt == 0) {
		//console.log("validation successful");

		$.ajax({
			type: 'GET',
			url: '/validateOrgUnitName',
			data: {
				'orgName': selOrg.val(), 'orgUnitName': orgunit.val()
			},
			success: function(data, textStatus, jqXHR) {
				if (data) {
					console.log(data);
					if(data == "true") {
						//console.log("validation success");

						var orgemrnoVal = $("#emrgnumPrefix").text() + orgemrno.val();
						$.ajax({
							type: "GET",
							url: '/createOrganizationUnit',
							data: {
								'orgUnitName': orgunit.val(), 'orgName': selOrg.val(), "orgUnitLocation": orgloc.val(), "orgUnitLatitude": orglat.val(), "orgUnitLongitude": orglong.val(), "orgUnitPhone": orgemrno.val()
							},
							success: function(data, textStatus, jqXHR) {
								if (data) {
									//console.log(data);
									$.notify(data,{ className: "success", position:"top center" });
									$('#createModal').modal('toggle');
									resetForm();
								}
							},
							error: function(jqXHR, textStatus, errorThrown) {
								$('#create-btn').notify(data,{ className: "error", position:"right" });
							}
						});

					}//orgname Validation
					else {
						$('#orgunit').notify('Organization unit name is not available',{ className: "error", position:"bottom" });
					}
				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				console.log(errorThrown);
				return "false";
			}
		});



	} 
}

function validateCreateDepartment() {
	var departmentName = $("#departmentName");
	var selOrgDep = $("#selOrgDep");
	var selOrgUnitDep = $("#selOrgUnitDep");

	var errCnt = 0;

	if(departmentName.val() == "") {
		departmentName.notify("Please enter the department name",{ position:"bottom" });
		errCnt++;
	} else {
		if(!isValidName(departmentName.val())) { 
			departmentName.notify("Please enter a valid department name",{ position:"bottom" });
			departmentName.focus();
			return false;
			errCnt++;
		}
	}
	if(selOrgDep.val() == "") {
		selOrgDep.notify("Please select an organization name",{ position:"bottom" });
		errCnt++;
	}

	if(selOrgUnitDep.val() == "") {
		selOrgUnitDep.notify("Please select an organization unit name",{ position:"bottom" });
		errCnt++;
	}

	if(errCnt == 0) {
		console.log("validation successful");
		$.ajax({
			type: 'GET',
			url: '/validateDeptName',
			data: {
				'orgName': selOrgDep.val(), 'orgUnitName': selOrgUnitDep.val(), 'deptName' : departmentName.val()
			},
			success: function(data, textStatus, jqXHR) {
				if (data) {
					console.log(data);
					if(data == "true") {
						//console.log("validation success");

						$.ajax({
							type: "GET",
							url: '/createDepartment',
							data: {
								'departmentName': departmentName.val(), 'orgUnitName': selOrgUnitDep.val(), 'orgName': selOrgDep.val()
							},
							success: function(data, textStatus, jqXHR) {
								if (data) {
									//console.log(data);
									$.notify(data,{ className: "success", position:"top center" });
									$('#createModal').modal('toggle');
									resetForm();
								}
							},
							error: function(jqXHR, textStatus, errorThrown) {
								$('#create-btn').notify(data,{ className: "error", position:"right" });
							}
						});

					}//orgname Validation
					else {
						$('#departmentName').notify('Department name is not available',{ className: "error", position:"bottom" });
					}
				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				console.log(errorThrown);
				return "false";
			}
		});

	}
}

function validateCreateRole() {
	var roleName = $("#roleName");
	var roleType = $("#roleType");
	var orgName = $("#selOrg-Role");
	var accessInfo = $("input[name='selAccessOU']:checked").attr("id");
	var orgUnitName = $("#selOrgUnit-Role");
	var depName = $("#selDep-Role");

	var errCnt = 0;

	if(roleName.val() == "") {
		roleName.notify("Please enter the role name",{ position:"bottom" });
		errCnt++;
	} else {
		if(!isValidName(roleName.val())) { 
			roleName.notify("Please enter a valid role name",{ position:"bottom" });
			roleName.focus();
			return false;
			errCnt++;
		}
	}
	if(roleType.val() == "") {
		roleType.notify("Please select the role type",{ position:"bottom" });
		errCnt++;
	}
	if(orgName.val() == "") {
		orgName.notify("Please select the organization",{ position:"bottom" });
		errCnt++;
	}
	if(accessInfo == "") {
		accessInfo.notify("Please provide the access information",{ position:"bottom" });
		errCnt++;
	} else {
		if(orgUnitName.val() == "") {
			orgUnitName.notify("Please select the organization unit",{ position:"bottom" });
			errCnt++;
		}
		/*if(depName.val() == "") {
			depName.notify("Please select the department",{ position:"bottom" });
			errCnt++;
		}*/
	}


	if(errCnt == 0) {
		console.log("validation successful");

		$.ajax({
			type: 'GET',
			url: '/validateRoleName',
			data: {
				'orgName': orgName.val(), 'roleName': roleName.val()
			},
			success: function(data, textStatus, jqXHR) {
				if (data) {
					console.log(data);
					if(data == "true") {
						//console.log("validation success");

						$.ajax({
							type: "GET",
							url: '/createRole',
							data: {
								"roleName" : roleName.val(),"roleType" : roleType.val(),"orgName" : orgName.val(),"accessInfo" : accessInfo,"orgUnitName": orgUnitName.val(),"depName" : depName.val()
							},
							success: function(data, textStatus, jqXHR) {
								if (data) {
									//console.log(data);
									$.notify(data,{ className: "success", position:"top center" });
									$('#createModal').modal('toggle');
									resetForm();
								}
							},
							error: function(jqXHR, textStatus, errorThrown) {
								$('#create-btn').notify(data,{ className: "error", position:"right" });
							}
						});

					}//orgname Validation
					else {
						$('#roleName').notify('Role name is not available',{ className: "error", position:"bottom" });
					}
				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				console.log(errorThrown);
				return "false";
			}
		});



	}
}

function validateCreateUser() {
	var user_first_name = $("#user-first-name");
	var user_last_name = $("#user-last-name");
	var user_name = $("#user-name");
	var user_password = $("#user-password");
	var user_conf_password = $("#user-conf-password");
	var user_email = $("#user-email");
	var user_qualification = $("#user-qualification");
	var user_specialization = $("#user-specialization");
	var user_employee_id = $("#user-employee-id");
	var user_aadhaar = $("#user-aadhaar");
	var user_pan = $("#user-pan");
	var user_passport = $("#user-passport");
	var user_phone = $("#user-phone");

	var user_org = $("#user-org");
	var user_org_unit = $("#user-org-unit");
	var user_role = $("#user-role");
	var user_department = $("#user-department");

	var genderInfo = $("input[name='user-gender']:checked").attr("id");
	var accessInfo = $("input[name='user-ou-access']:checked").attr("id");
	var universalAccessInfo = $("input[name='user-universal-access']:checked").attr("id");


	var errCnt = 0;

	var loginFlag = window.localStorage.getItem("loginFlag");

	if(user_first_name.val() == "") {
		user_first_name.notify("Please enter the first name",{ position:"bottom" });
		errCnt++;
	} else {
		if(!isValidNameWithSpace(user_first_name.val())) { 
			user_first_name.notify("Please enter a valid name",{ position:"bottom" });
			user_first_name.focus();
			return false;
			errCnt++;
		}
	}
	if(user_last_name.val() == "") {
		user_last_name.notify("Please enter the last name",{ position:"bottom" });
		errCnt++;
	} else {
		if(!isValidName(user_last_name.val())) { 
			user_last_name.notify("Please enter a valid name",{ position:"bottom" });
			user_last_name.focus();
			return false;
			errCnt++;
		}
	}

	if(user_name.val() == "") {
		user_name.notify("Please enter the user name",{ position:"bottom" });
		errCnt++;
	} else {
		if(!isValidUserName(user_name.val())) { 
			user_name.notify("Please enter a valid user name",{ position:"bottom" });
			user_name.focus();
			return false;
			errCnt++;
		}
	}

	if(user_password.val() == "") {
		user_password.notify("Please enter the password",{ position:"bottom" });
		errCnt++;
	} else {
		if(!isValidPassword(user_password.val())) { 
			user_password.notify("Please enter a valid password. (Your password must contain at least 10 characters made up of at least one lowercase letter, at least one uppercase letter, at least one number, and at least one symbol)", {
				style: 'multi-line-text'
			});
			//user_password.notify("Please enter a valid password. (Password must contain at least one letter, at least one number, and be longer than 5 charaters.)",{ position:"top" });
			user_password.focus();
			return false;
			errCnt++;
		}
	}


	if(user_email.val() == "") {
		user_email.notify("Please enter the email",{ position:"bottom" });
		errCnt++;
	} else {
		if(!isValidEmail(user_email.val())) { 
			user_email.notify("Please enter a valid email",{ position:"bottom" });
			user_email.focus();
			return false;
			errCnt++;
		}
	}

	if(loginFlag == "Prod") {

		if(user_conf_password.val() == "") {
			user_conf_password.notify("Please re-enter the password",{ position:"bottom" });
			errCnt++;
		} else {
			if(!isValidConfPassword(user_conf_password.val(), user_password.val())) { 
				user_conf_password.notify("Please enter the same password as above",{ position:"bottom" });
				user_conf_password.focus();
				return false;
				errCnt++;
			}
		}

		if(user_qualification.val() == "") {
			user_qualification.notify("Please enter the qualification",{ position:"bottom" });
			errCnt++;
		}

		if(user_specialization.val() == "") {
			user_specialization.notify("Please select the specialization",{ position:"bottom" });
			errCnt++;
		}

		if(user_employee_id.val() == "") {
			user_employee_id.notify("Please enter the employee id",{ position:"bottom" });
			errCnt++;
		}

		if(user_aadhaar.val() == "") {
			user_aadhaar.notify("Please enter the Aadhaar number",{ position:"bottom" });
			errCnt++;
		} else {
			if(!isValidAadhar(user_aadhaar.val())) { 
				user_aadhaar.notify("Please enter a valid Aadhaar number (1234 1234 1234)",{ position:"bottom" });
				user_aadhaar.focus();
				return false;
				errCnt++;
			}
		}

		if(user_pan.val() == "") {
			user_pan.notify("Please enter the PAN number",{ position:"bottom" });
			errCnt++;
		} else {
			if(!isValidPAN(user_pan.val())) { 
				user_pan.notify("Please enter a valid PAN number (ABCDE1234M)",{ position:"bottom" });
				user_pan.focus();
				return false;
				errCnt++;
			}
		}

		if(user_passport.val() == "") {
			user_passport.notify("Please enter the Passport number",{ position:"bottom" });
			errCnt++;
		} else {
			if(!isValidPassportNumber(user_passport.val())) { 
				user_passport.notify("Please enter a valid Passport number (P1234567)",{ position:"bottom" });
				user_passport.focus();
				return false;
				errCnt++;
			}
		}

		if(user_phone.val() == "") {
			user_phone.notify("Please enter the phone number",{ position:"bottom" });
			errCnt++;
		} else {
			if(!isValidPhone(user_phone.val())) { 
				user_phone.notify("Please enter a valid phone number",{ position:"bottom" });
				user_phone.focus();
				return false;
				errCnt++;
			}
		}
	}


	if(user_org.val() == "") {
		user_org.notify("Please select the organization",{ position:"bottom" });
		errCnt++;
	}
	if(user_org_unit.val() == "") {
		user_org_unit.notify("Please select the organization unit",{ position:"bottom" });
		errCnt++;
	}
	if(user_role.val() == "") {
		user_role.notify("Please select a role",{ position:"bottom" });
		errCnt++;
	}
	if(user_department.val() == "") {
		user_department.notify("Please select the department",{ position:"bottom" });
		errCnt++;
	}

	if(genderInfo == "") {
		genderInfo.notify("Please provide the gender information",{ position:"bottom" });
		errCnt++;
	}
	if(accessInfo == "") {
		accessInfo.notify("Please provide the OU access information",{ position:"bottom" });
		errCnt++;
	}
	if(universalAccessInfo == "") {
		universalAccessInfo.notify("Please provide the universal access information",{ position:"bottom" });
		errCnt++;
	}


	if(errCnt == 0) {
		console.log("validation successful");

		$.ajax({
			type: 'GET',
			url: '/validateUsername',
			data: {
				'orgName': user_org.val(), 'userName': user_name.val()
			},
			success: function(data, textStatus, jqXHR) {
				if (data) {
					console.log(data);
					if(data == "true") {
						//console.log("validation success");

						var user_phone_number = $("#user-phone-prefix").text() + user_phone.val();

						$.ajax({
							type: "GET",
							url: '/createUser',
							data: {
								"user_first_name": user_first_name.val(), "user_last_name": user_last_name.val(), "user_name": user_name.val(),"user_password": user_password.val(),"user_email": user_email.val(),"user_qualification": user_qualification.val(),"user_specialization": user_specialization.val(),"user_employee_id": user_employee_id.val(),"user_aadhaar": user_aadhaar.val(),"user_pan": user_pan.val(),"user_passport": user_passport.val(),"user_phone": user_phone_number,"user_org": user_org.val(),"user_org_unit": user_org_unit.val(),"user_role": user_role.val(),"user_department": user_department.val(),"genderInfo": genderInfo,"accessInfo": accessInfo,"universalAccessInfo": universalAccessInfo
							},
							success: function(data, textStatus, jqXHR) {
								if (data) {
									//console.log(data);
									$.notify(data,{ className: "success", position:"top center" });
									$('#createModal').modal('toggle');
									resetForm();
								}
							},
							error: function(jqXHR, textStatus, errorThrown) {
								$('#create-btn').notify(data,{ className: "error", position:"right" });
							}
						});

					}//orgname Validation
					else {
						$('#user-name').focus();
						$('#user-name').notify('Username is not available',{ className: "error", position:"bottom" });
					}
				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				console.log(errorThrown);
				return "false";
			}
		});




	}
}

function validateCreateTab() {
	var tab_name = $("#tab-name");
	var tab_header = $("#tab-header");
	var tab_purpose = $("#tab-purpose");
	var tab_template = $("#tab-template");
	var orgName = $("#tab-org");
	var accessInfo = $("input[name='tab-ou-specific']:checked").attr("id");
	var accessInfoDep = $("input[name='tab-dep-specific']:checked").attr("id");
	var tab_org_unit = $("#tab-org-unit");
	var tab_department = $("#tab-department");
	var tab_role = $("#tab-role");

	var errCnt = 0;

	if(tab_name.val() == "") {
		tab_name.notify("Please enter the tab name",{ position:"bottom" });
		errCnt++;
	} else {
		if(!isValidName(tab_name.val())) { 
			tab_name.notify("Please enter a valid tab name",{ position:"bottom" });
			tab_name.focus();
			return false;
			errCnt++;
		}
	}
	if(tab_header.val() == "") {
		tab_header.notify("Please select the tab header",{ position:"bottom" });
		errCnt++;
	}
	if(tab_purpose.val() == "") {
		tab_purpose.notify("Please select the tab purpose",{ position:"bottom" });
		errCnt++;
	}
	if(tab_template == "") {
		tab_template.notify("Please select a tab template",{ position:"bottom" });
		errCnt++;
	}
	if(orgName.val() == "") {
		orgName.notify("Please select the organization",{ position:"bottom" });
		errCnt++;
	}
	if(tab_org_unit.val() == "") {
		tab_org_unit.notify("Please select the organization unit",{ position:"bottom" });
		errCnt++;
	}
	if(tab_department.val() == "") {
		tab_department.notify("Please select the department",{ position:"bottom" });
		errCnt++;
	}
	if(tab_role.val() == "") {
		tab_role.notify("Please select a role",{ position:"bottom" });
		errCnt++;
	}
	if(accessInfo == "") {
		accessInfo.notify("Please select the access info",{ position:"bottom" });
		errCnt++;
	}

	if(errCnt == 0) {
		console.log("validation successful");

		$.ajax({
			type: 'GET',
			url: '/validateTabname',
			data: {
				'orgName': orgName.val(), 'tabName': tab_name.val()
			},
			success: function(data, textStatus, jqXHR) {
				if (data) {
					console.log(data);
					if(data == "true") {
						//console.log("validation success");

						$.ajax({
							type: "GET",
							url: '/createTab',
							data: {
								"tab_name" : tab_name.val(),"tab_header" : tab_header.val(),"tab_purpose" : tab_purpose.val(),"tab_template" : tab_template.val(),"orgName" : orgName.val(),"accessInfo" : accessInfo,"tab_org_unit" : tab_org_unit.val(),"tab_department" : tab_department.val(),"tab_role" : tab_role.val(), "accessInfoDep" : accessInfoDep
							},
							success: function(data, textStatus, jqXHR) {
								if (data) {
									//console.log(data);
									$.notify(data,{ className: "success", position:"top center" });
									$('#createModal').modal('toggle');
									resetForm();
								}
							},
							error: function(jqXHR, textStatus, errorThrown) {
								$('#create-btn').notify(data,{ className: "error", position:"right" });
							}
						});

					}//orgname Validation
					else {
						$('#tab-name').focus();
						$('#tab-name').notify('Tabname is not available',{ className: "error", position:"bottom" });
					}
				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				console.log(errorThrown);
				return "false";
			}
		});



	}
}


// validations and Post request for create news

function validateCreateNews(){
	var content= myEditor.getData();
	var selOrg = $("#selOrgNews");
	var selOrgUnit = $("#selOrgUnitNews");
	var selTab = $("#selTabNews");
	var editor=$("#editor");

	var errCnt = 0;

	if(content == "") {
		editor.notify("Please enter some text",{ position:"bottom" });
		errCnt++;
	} 
	if(selOrg.val() == "") {
		selOrg.notify("Please select an organization",{ position:"bottom" });
		errCnt++;
	}

	if(selOrgUnit.val() == "") {
		selOrgUnit.notify("Please select an organization unit ",{ position:"bottom" });
		errCnt++;
	}
	if(selTab.val() == "") {
		selTab.notify("Please select an Tab ",{ position:"bottom" });
		errCnt++;
	}

	if(errCnt == 0) {
		$.ajax({
			type: "POST",
			url: '/createNews',
			data: {
				'content' :content,'orgUnitName': selOrgUnit.val(), 'orgName': selOrg.val(), 'tabName':selTab.val()
			},		
			success: function(data, textStatus, jqXHR) {
				if (data) {
					//console.log(data);
					$.notify(data,{ className: "success", position:"top center" });
					$('#newsModal').modal('toggle');
					resetForm();
					myEditor.setData('');

				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				$('#create-news-btn').notify(data,{ className: "error", position:"right" });
			}
		});
	}


}

//validation and post req for survey

function validateSurvey(){
	//var template=$("#survey-tab-template");
	var selOrg = $("#selOrgSurvey");
	var selOrgUnit = $("#selOrgUnitSurvey");
	var selTab = $("#selTabSurvey");
	var startDate=$("#startDate");
	var endDate=$("#endDate");
	var surveyName=$("#surveyName");
	var tbody=$(".table");
	var multipleQuestions = [];
	$('#surveyBody tr').each(function (i, j) {
		var question = $('#question', j).text();
		var type = $('#types', j).text();
		var options=$('#options',j).text();
		multipleQuestions.push({ question: question, type: type,answers:options });

	});
	var questions=JSON.stringify(multipleQuestions);
	console.log(questions);
	console.log(startDate.val()+"\n"+endDate.val());

	var errCnt = 0;

	if(surveyName.val() == "") {
		surveyName.notify("Please enter survey name",{ position:"bottom" });
		errCnt++;
	}

	if(selOrg.val() == "") {
		selOrg.notify("Please select an organization",{ position:"bottom" });
		errCnt++;
	}

	if(selOrgUnit.val() == "") {
		selOrgUnit.notify("Please select an organization unit ",{ position:"bottom" });
		errCnt++;
	}
	if(selTab.val() == "") {
		selTab.notify("Please select an Tab ",{ position:"bottom" });
		errCnt++;
	}
	if(startDate.val() == "") {
		startDate.notify("Please select Date",{ position:"bottom" });
		errCnt++;
	}

	if(endDate.val() == "") {
		endDate.notify("Please select Date ",{ position:"bottom" });
		errCnt++;
	}
	if(questions == "") {
		tbody.notify("Please add questions ",{ position:"bottom" });
		errCnt++;
	}

	if(errCnt == 0) {
		var requestData={
				'orgUnitName': selOrgUnit.val(), 'orgName': selOrg.val(), 'tabName':selTab.val(),'startDate':formateDatewithSlash(startDate.val()),'endDate':formateDatewithSlash(endDate.val()),'surveyName':surveyName.val(),'question':multipleQuestions
		};
		//console.log(JSON.stringify(requestData));

		$.ajax({
			type: "POST",
			dataType: 'json',
			url: '/api/createSurvey',
			data: JSON.stringify(requestData),	
			contentType: "application/json; charset=utf-8",	
			success: function(data, textStatus, jqXHR) {
				if (data) {
					//console.log(data);
					//console.log(data.statusString);
					$.notify(data.statusString,{ className: "success", position:"top center" });
					$('#createSurveyModal').modal('toggle');
					resetForm();
					$("input[type=date]").val("");
					$("#tbody").remove();

				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				$('##view-modal-body').notify(data.statusString,{ className: "error", position:"right" });
			}
		});

	}



}

//View Functions
function getList(key) {
	if(key == "view-organization-list") {
		$.ajax({
			type: "GET",
			url: '/getOrganizationList',
			data: {
				'displayFlag': "page"
			},
			success: function(data, textStatus, jqXHR) {
				if (data) {
					//console.log(data);
					$("#view-modal-body").html(data);
				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				$("#view-modal-body").html("<h3>" + errorThrown + "</h3>");
			}
		});
	}
	if(key == "view-organization-unit-list") {
		$.ajax({
			type: "GET",
			url: '/getOrganizationUnitList',
			data: {
				"displayFlag": "page", "orgName": ""
			},
			success: function(data, textStatus, jqXHR) {
				if (data) {
					//console.log(data);
					$("#view-modal-body").html(data);
				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				$("#view-modal-body").html("<h3>" + errorThrown + "</h3>");
			}
		});
	}

	if(key == "view-department-list") {
		$.ajax({
			type: "GET",
			url: '/getDepartmentList',
			data: {
				"displayFlag": "page", "orgName": "", "orgUnitName": ""
			},
			success: function(data, textStatus, jqXHR) {
				if (data) {
					$("#view-modal-body").html(data);
				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				$("#view-modal-body").html("<h3>" + errorThrown + "</h3>");
			}
		});
	}

	if(key == "view-role-list") {
		$.ajax({
			type: "GET",
			url: '/getRoleList',
			data: {
				'displayFlag': "page", "orgName": ""
			},
			success: function(data, textStatus, jqXHR) {
				if (data) {
					//console.log(data);
					$("#view-modal-body").html(data);
				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				$("#view-modal-body").html("<h3>" + errorThrown + "</h3>");
			}
		});
	}

	if(key == "view-user-list") {
		$.ajax({
			type: "GET",
			url: '/getUserList',
			data: {
				'displayFlag': "page", "orgName": ""
			},
			success: function(data, textStatus, jqXHR) {
				if (data) {
					//console.log(data);
					$("#view-xl-modal-body").html(data);
				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				$("#view-xl-modal-body").html("<h3>" + errorThrown + "</h3>");
			}
		});
	}

	if(key == "view-tab-list") {
		$.ajax({
			type: "GET",
			url: '/getTabList',
			data: {
				'displayFlag': "page", "orgName": ""
			},
			success: function(data, textStatus, jqXHR) {
				if (data) {
					//console.log(data);
					$("#view-xl-modal-body").html(data);
				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				$("#view-xl-modal-body").html("<h3>" + errorThrown + "</h3>");
			}
		});
	}

	if(key == "view-survey-list") {
		$.ajax({
			type: "GET",
			url: '/getSurvey',
			success: function(data, textStatus, jqXHR) {
				if (data) {
					//console.log(data);
					$("#view-xl-modal-body").html(data);
				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				$("#view-xl-modal-body").html("<h3>" + errorThrown + "</h3>");
			}
		});
	}
}

function updateOrganizationList(modalContent) {
	if(modalContent == "create-organization-unit") {
		getOrgList("selOrgOU");	  	
	}
	if(modalContent == "create-department") {
		getOrgList("selOrgDep");	  	
		setTimeout(function() {
			loadOrgUnitAlternate($("#selOrgDep"), "selOrgUnitDep");
		}, 1500);
	}
	if(modalContent == "create-role") {
		getOrgList("selOrg-Role");	  	
		setTimeout(function() {
			loadOrgUnitAlternate($("#selOrg-Role"), "selOrgUnit-Role");
		}, 1500);
		initializeRoleValidator();
	}
	if(modalContent == "create-user") {
		getOrgList("user-org");	  	
		setTimeout(function() {
			loadOrgUnitAlternate($("#user-org"), "user-org-unit");
			loadRolesAlternate($("#user-org"), "user-role");
		}, 1500);
		setTimeout(function() {
			loadDepartmentListAlternate($("#user-org-unit"), "user-org", "user-department");
		}, 2000);
	}
	if(modalContent == "create-tab") {
		getOrgList("tab-org");	  	
		setTimeout(function() {
			loadOrgUnitAlternate($("#tab-org"), "tab-org-unit");
			loadRolesAlternate($("#tab-org"), "tab-role");
			loadTabTemplates($("#tab-org"), "tab-template");
		}, 1500);
		setTimeout(function() {
			loadDepartmentListAlternate($("#tab-org-unit"), "tab-org", "tab-department");
		}, 2000);
		initializeTabValidator();
	}

	if(modalContent == "assign-role-to-user") {
		getOrgList("selOrgAssignUserRole");
		setTimeout(function() {
			loadRolesAvailable4AssignRoles2User("selOrgAssignUserRole");
		}, 800);

	}

	if(modalContent == "assign-role-to-tab") {
		getOrgList("selOrgAssignTabRole");
		setTimeout(function() {
			loadOrgUnitAlternate($("#selOrgAssignTabRole"), "selOrgUnitAssignTabRole");
			loadRolesAlternate($("#selOrgAssignTabRole"), "selRoleAssignTabRole");
		}, 500);  	
	}

	if(modalContent == "create-news" ){
		setTimeout(function() {
			getOrgList("selOrgNews");
		}, 500);
		setTimeout(function() {
			loadOrgUnitAlternate($("#selOrgNews"), "selOrgUnitNews");
		}, 800);
		setTimeout(function() {
			loadTabsOrgUnitAlternate($("#selOrgUnitNews"),"selOrgNews","selTabNews");
		}, 1000); 

	}
	if(modalContent == "create-survey" ){
		getOrgList("selOrgSurvey");
		setTimeout(function() {
			loadOrgUnitAlternate($("#selOrgSurvey"), "selOrgUnitSurvey");
		}, 800);
		setTimeout(function() {
			loadTabsOrgUnitAlternate($("#selOrgUnitSurvey"),"selOrgSurvey","selTabSurvey");
		}, 1000); 

	}
	/*if(modalContent == "edit-Survey") {
  	getOrgList("selOrgOU");	  	
  }*/
}

function initializeRoleValidator() {
	$('input[type=radio][name=selAccessOU]').change(function() {
		var selection = $(this).prop('id');
		if(selection.includes("no")) {
			$(".accessBasedHide4Role").hide();
		} else {
			$(".accessBasedHide4Role").show();
		}
	});
}

function initializeTabValidator() {
	$('input[type=radio][name=tab-ou-specific]').change(function() {
		var selection = $(this).prop('id');
		if(selection.includes("no")) {
			$(".accessBasedHide4TabOU").hide();
		} else {
			$(".accessBasedHide4TabOU").show();
		}
	});

	$('input[type=radio][name=tab-dep-specific]').change(function() {
		var selection = $(this).prop('id');
		if(selection.includes("no")) {
			$(".accessBasedHide4TabDep").hide();
		} else {
			$(".accessBasedHide4TabDep").show();
		}
	});
}

//View Functions

function getOrgList(selectID) {
	$.ajax({
		type: "GET",
		url: '/getOrganizationList',
		data: {
			'displayFlag': "list"
		}, 
		success: function(data, textStatus, jqXHR) {
			if (data) {
				$('#'+selectID).empty();
				var orglist = data.split(","); 
				$.each(orglist, function(key, value) {  
					//console.log(value);
					$('#'+selectID)
					.append($("<option></option>")
							.attr("value",value)
							.text(value)); 
				});
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.log("Unable to read the list");
		}
	});
}

function loadOrgUnits(orgDropdown) {
	//console.log(orgDropdown);
	var selOrg = $(orgDropdown).val();	
	$.ajax({
		type: "GET",
		url: '/getOrganizationUnitList',
		data: {
			"displayFlag": "page", "orgName": selOrg
		},
		success: function(data, textStatus, jqXHR) {
			if (data) {
				console.log(data);
				$("#view-modal-body").html(data);
				$('select[id="selOrg-OU"]').val(selOrg);
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("#view-modal-body").html("<h3>" + errorThrown + "</h3>");
		}
	});
}

function loadOrgUnitAlternate(orgDropdown, selectID) {

	//console.log(orgDropdown);
	var selOrg = $(orgDropdown).val();
	$.ajax({
		type: "GET",
		url: '/getOrganizationUnitList',
		data: {
			"displayFlag": "list", "orgName": selOrg
		},
		success: function(data, textStatus, jqXHR) {
			if (data) {
				$('#'+selectID).empty();
				var orglist = data.split(",");
				$.each(orglist, function(key, value) {   
					$('#'+selectID)
					.append($("<option></option>")
							.attr("value",value)
							.text(value)); 
				});
				if(selectID == "selOrgUnit-Dep") {
					loadDepartmentListAlternateTable($("#"+selectID), "selOrg-Dep", "resultTable");
				}
				if(selectID == "selOrgUnit-Role") {
					loadDepartmentListAlternate($("#"+selectID), "selOrg-Role", "selDep-Role");
				}
				if(selectID == "user-org-unit") {
					loadDepartmentListAlternate($("#"+selectID), "user-org", "user-department");
					loadRolesAlternate($(orgDropdown), "user-role");
				}
				if(selectID == "tab-org-unit") {
					loadDepartmentListAlternate($("#"+selectID), "tab-org", "tab-department");
					loadRolesAlternate($(orgDropdown), "tab-role");
					loadTabTemplates($(orgDropdown), "tab-template");
				}
				if(selectID =="selOrgUnitNews"){
					loadTabsOrgUnitAlternate($("#selOrgUnitNews"),"selOrgNews","selTabNews");		
				}
				if(selectID =="selOrgUnitSurvey"){
					loadTabsOrgUnitAlternate($("#selOrgUnitSurvey"),"selOrgSurvey","selTabSurvey");		
				}
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("#view-modal-body").html("<h3>" + errorThrown + "</h3>");
		}
	});
}

function loadDepartmentListAlternateTable(orgDropdown, orgNameSelectID, selectID) {
	//console.log(orgDropdown);
	var selOrgUnit = $(orgDropdown).val();
	var selOrg = $("#"+orgNameSelectID).val();
	//console.log(selOrg);
	$.ajax({
		type: "GET",
		url: '/getDepartmentList',
		data: {
			"displayFlag": "list", "orgName": selOrg, "orgUnitName": selOrgUnit
		},
		success: function(data, textStatus, jqXHR) {
			if (data) {
				//console.log(data.length);
				if(data.length > 0) {
					$("#" + selectID + " tr").remove();
					var depList = data.split(",");
					$.each(depList, function(key, value) {   
						$('#'+selectID)
						.append("<tr><td>"+value+"</td></tr>")
					});
				} else {
					$("#"+selectID+" tr").remove();
					$('#'+selectID)
					.append("<tr><td>No Departments Available</td></tr>")
				}
			} else {
				$("#"+selectID+" tr").remove();
				$('#'+selectID)
				.append("<tr><td>No Departments Available</td></tr>")
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("#view-modal-body").html("<h3>" + errorThrown + "</h3>");
		}
	});
}

function loadDepartmentListAlternate(orgDropdown, orgNameSelectID, selectID) {
	//console.log(orgDropdown);
	var selOrgUnit = $(orgDropdown).val();
	var selOrg = $("#"+orgNameSelectID).val();
	//console.log(selOrg);
	$.ajax({
		type: "GET",
		url: '/getDepartmentList',
		data: {
			"displayFlag": "list", "orgName": selOrg, "orgUnitName": selOrgUnit
		},
		success: function(data, textStatus, jqXHR) {
			if (data) {
				//console.log(data.length);
				if(data.length > 0) {
					$('#'+selectID).empty();
					var orglist = data.split(",");
					$.each(orglist, function(key, value) {   
						$('#'+selectID)
						.append($("<option></option>")
								.attr("value",value)
								.text(value)); 
					});
				} else {
					$('#'+selectID).empty();
				}
			} else {
				$('#'+selectID).empty();
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("#view-modal-body").html("<h3>" + errorThrown + "</h3>");
		}
	});
}


//Tabs based on the OU

function loadTabsOrgUnitAlternate(orgUnitDropDown,orgNameSelectID,selectID) {
	var selOrgUnit = $(orgUnitDropDown).val();
	var selOrg = $("#"+orgNameSelectID).val();
	var tabType;
	if(selectID=="selTabNews"){
		tabType="News";
	}else{
		tabType="Survey";
	}
	//console.log(tabType);
	$.ajax({
		type: "GET",
		url: '/getTabsBasedOnOU',
		data: {
			"orgName": selOrg, "orgUnit": selOrgUnit,"has_all_ou_access" : "ou_specific_no", 'displayFlag': "list","tabType":tabType
		},
		success: function(data, textStatus, jqXHR) {
			//console.log(data);
			if (data) {
				if(data.length>0){
					$('#'+selectID).empty();
					var orglist = data.split(",");
					//	console.log(orglist);
					$.each(orglist, function(key, value) { 
						//console.log("orglist"+orglist);
						$('#'+selectID)
						.append($("<option></option>")
								.attr("value",value)
								.text(value)); 
					});
				}else{
					$('#'+selectID).empty();
				}
			}else{
				$('#'+selectID).empty();
			} 			
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("#view-modal-body").html("<h3>" + errorThrown + "</h3>");
		}
	}); 
}

function loadRoles(orgDropdown) {
	//console.log(orgDropdown);
	var selOrg = $(orgDropdown).val();	
	$.ajax({
		type: "GET",
		url: '/getRoleList',
		data: {
			"displayFlag": "page", "orgName": selOrg
		},
		success: function(data, textStatus, jqXHR) {
			if (data) {
				//console.log(data);
				$("#view-modal-body").html(data);
				$('select[id="selOrg-Role"]').val(selOrg);
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("#view-modal-body").html("<h3>" + errorThrown + "</h3>");
		}
	});
}
function loadRolesAlternate(orgDropdown, destDropdownID) {
	//console.log(orgDropdown);
	var selOrg = $(orgDropdown).val();	
	$.ajax({
		type: "GET",
		url: '/getRoleList',
		data: {
			"displayFlag": "list", "orgName": selOrg
		},
		success: function(data, textStatus, jqXHR) {
			if (data) {
				if(data.length > 0) {
					$('#'+destDropdownID).empty();
					var orglist = data.split(",");
					$.each(orglist, function(key, value) {   
						$('#'+destDropdownID)
						.append($("<option></option>")
								.attr("value",value)
								.text(value)); 
					});
				} else {
					$('#'+destDropdownID).empty();
				}
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("#view-modal-body").html("<h3>" + errorThrown + "</h3>");
		}
	});
}

function loadRolesAvailable4AssignRoles2User(orgName) {
	$("#searchUser").val("");
	loadRolesAvailable(orgName);
	//setTimeout(initializeToggleClick4Role, 1500);
}

function loadRolesAvailable(orgName) {
	//console.log(orgDropdown);
	var selOrg = $("#"+orgName).val();
	var selUser = $("#searchUser").val();

	$.ajax({
		type: "GET",
		url: '/getRoleList',
		data: {
			"displayFlag": "list", "orgName": selOrg
		},
		success: function(data, textStatus, jqXHR) {
			if (data) {
				if(selUser.length <= 0) {
					$('.group-assigned li').remove();
					$('.group-assigned').append('<li class="list-group-item list-group-item-danger">No Roles Available - Please select a User</li>');
				}
				if(data.length > 0) {
					var orglist = data.split(",");
					$('.group-available li').remove();
					$.each(orglist, function(index, roleName) {
						$('.group-available').append('<li class="list-group-item li-item list-group-item-success">' + roleName + '</li>')
					});

				} else {
					$('.group-available li').remove();
					$('.group-available').append('<li class="list-group-item list-group-item-danger">No Roles Available</li>');								
				}
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("#view-modal-body").html("<h3>" + errorThrown + "</h3>");
		}
	});
}

function loadUserRoles(orgName, username) {
	//console.log(orgDropdown);
	var selOrg = $("#"+orgName).val();
	//

	$.ajax({
		type: "GET",
		url: '/getUserRoles',
		data: {
			"displayFlag": "list", "orgName": selOrg, "userName": username
		},
		success: function(data, textStatus, jqXHR) {
			if (data) {
				if(data.length > 0) {
					var rolelist = data.split(",");
					$('.group-assigned li').remove();
					$.each(rolelist, function(index, roleName) {
						$('.group-assigned').append('<li class="list-group-item li-item list-group-item-primary">' + roleName + '</li>')
					});

				} else {
					$('.group-assigned li').remove();
					$('.group-assigned').append('<li class="list-group-item list-group-item-danger">No Roles Available</li>');
				}
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("#view-modal-body").html("<h3>" + errorThrown + "</h3>");
		}
	});
}

function loadTabs(orgDropdown, flag) {
	var selOrg = $(orgDropdown).val();
	//console.log(selOrg);

	$.ajax({
		type: "GET",
		url: '/getTabList',
		data: {
			'displayFlag': "page", "orgName": selOrg
		},
		success: function(data, textStatus, jqXHR) {
			//console.log(data);
			if (data) {
				//console.log(data);
				$("#view-xl-modal-body").html(data);
				$('select[id="selOrg-Tab"]').val(selOrg);
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("#view-xl-modal-body").html("<h3>" + errorThrown + "</h3>");
		}
	});
}


function loadUsers(orgDropdown, flag) {
	//console.log(orgDropdown);
	var selOrg = $(orgDropdown).val();
	//console.log(selOrg);
	$.ajax({
		type: "GET",
		url: '/getUserList',
		data: {
			"displayFlag": flag, "orgName": selOrg
		},
		success: function(data, textStatus, jqXHR) {
			if (data) {
				$("#view-xl-modal-body").html(data);
				$('select[id="selOrg-User"]').val(selOrg);
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("#view-xl-modal-body").html("<h3>" + errorThrown + "</h3>");
		}
	});
}

function deleteUser(btn, orgDropdownId, username) {
	//console.log(orgDropdown);
	if(confirm("Do you want to really delete this User?")) {
		var selOrg = $("#"+orgDropdownId).val();	
		$.ajax({
			type: "DELETE",
			url: '/deleteUser',
			data: {
				"orgName": selOrg, "userName": username
			},
			success: function(data, textStatus, jqXHR) {
				if (data) {
					$.notify(data, { className: "success", position:"top center" });
					$(btn).closest("tr").remove();
				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				$.notify(errorThrown, { position:"top center" });
			}
		});
	}		
}

function loadTabTemplates(orgDropdown, destDropdownID) {
	//console.log(orgDropdown);
	var selOrg = $(orgDropdown).val();	
	$.ajax({
		type: "GET",
		url: '/getTabTemplateList',
		data: {
			"displayFlag": "list", "orgName": selOrg
		},
		success: function(data, textStatus, jqXHR) {
			if (data) {
				if(data.length > 0) {
					$('#'+destDropdownID).empty();
					var orglist = data.split(",");
					$.each(orglist, function(key, value) {   
						$('#'+destDropdownID)
						.append($("<option></option>")
								.attr("value",value)
								.text(value)); 
					});
				} else {
					$('#'+destDropdownID).empty();
				}
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("#view-modal-body").html("<h3>" + errorThrown + "</h3>");
		}
	});
}

function getTabs(purpose, org, orgUnit, include_dep, dep, role, ou_flag, dep_flag) {
	//console.log(orgDropdown);
	//var selOrg = $("#"+orgName).val();	
	$.ajax({
		type: "GET",
		url: '/getTabs',
		data: {
			"purpose": purpose, "orgName": org, "orgUnit": orgUnit, "includeDepFlag": include_dep, "department": dep, "role": role, "has_all_ou_access" : ou_flag, "has_all_dep_access": dep_flag
		},
		success: function(data, textStatus, jqXHR) {
			if (data) {
				//console.log(data);
				if(purpose == "assigned") {
					$('.tab-list-left ul li').remove();
					$(".tab-list-left ul").append(data);
				}

				if(purpose == "available") {
					$('.tab-list-right ul li').remove();
					$(".tab-list-right ul").append(data);
				}
				/*if(data.length > 0) {
            		var rolelist = data.split(",");
            		$('.group-assigned li').remove();
					$.each(rolelist, function(index, roleName) {
						$('.group-assigned').append('<li class="list-group-item li-item list-group-item-primary">' + roleName + '</li>')
					});

				} else {
					$('.group-assigned li').remove();
					$('.group-assigned').append('<li class="list-group-item list-group-item-danger">No Roles Available</li>');
				}*/
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("#view-modal-body").html("<h3>" + errorThrown + "</h3>");
		}
	});
}

//Utility Functions
function resetForm() {
	$("input[type=text]").val("");
	$("input[type=password]").val("");
	$("input[type=email]").val("");
	$("textarea").val("");

}

function isValidName(name) {
	var regEx = /^[a-zA-Z][a-zA-Z0-9-_\.]{1,20}$/; //Minimum 2-20 characters starts with string
	return regEx.test(String(name));
}

function isValidEmail(email) {
	var regEx = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	return regEx.test(String(email).toLowerCase());
}

function isValidPhone(phone) {
	var regEx = /^\d{10}$/;
	return regEx.test(String(phone));
}

function isValidNameWithSpace(nameWithSpace) {
	var regEx = /^[a-zA-Z ]*$/ //Alphabets with space
		return regEx.test(String(name));
}

function isValidUserName(username) {
	var regEx = /^[a-zA-Z0-9_.]{5,}[a-zA-Z]*[0-9]*$/;
	return regEx.test(String(username));
}

function isValidPassword(password) {
	//Password must contain at least one letter, at least one number, and be longer than five charaters.
	//var regEx = /^(?=.*[0-9]+.*)(?=.*[a-zA-Z]+.*)[0-9a-zA-Z]{5,}$/;
	//Your password must contain at least 10 characters made up of at least one lowercase letter, at least one uppercase letter, at least one number, and at least one symbol
	var regEx = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[~!@#$%])[A-Za-z\d~!@#$%]{10,64}$/;
	return regEx.test(String(password));
}

function isValidConfPassword(repassword, orgpassword) {
	if(repassword != "" && orgpassword != "") {
		if(repassword == orgpassword) {
			return true;
		}
	} 
	return false;
}

function isValidAadhar(aadharNum) {
	var regEx = /^\d{4}\s\d{4}\s\d{4}$/;
	return regEx.test(String(aadharNum));
}

function isValidPAN(panNum) {
	var regEx = /^([a-zA-Z]){5}([0-9]){4}([a-zA-Z]){1}?$/;
	return regEx.test(String(panNum));
}

function isValidPassportNumber(psNum) {
	var regEx = /^(?!^0+$)[a-zA-Z0-9]{3,20}$/;
	return regEx.test(String(psNum));
}



//----------------------------------------------------------------------------//

function hideAssignedRoles() {
	//console.log("hideAssignedRoles");
	var assignedRoles = [];
	var listItems = $(".group-assigned li");
	listItems.each(function(idx, li) {
		var role = $(li).text();
		//console.log(role);
		assignedRoles.push(role);
	});

	//console.log(assignedRoles);

	var listItemsAvailable = $(".group-available li");
	listItemsAvailable.each(function(idx, li) {
		var avRole = $(li).text();
		var liIndex = $.inArray(avRole, assignedRoles);
		console.log(liIndex);
		if(liIndex >= 0) {
			console.log(avRole);
			$(li).remove();
		}
	});

	$(".group-available").show();
}

function addRoles4User(orgName, username, roleArray) {
	//console.log(orgDropdown);
	var selOrg = $("#"+orgName).val();	
	$.ajax({
		type: "GET",
		url: '/addRoles4User',
		data: {
			"orgName": selOrg, "userName": username, "roles" : roleArray.toString()
		},
		success: function(data, textStatus, jqXHR) {
			if (data) {
				//console.log(data);
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.log(errorThrown);
		}
	});
}

function removeRoles4User(orgName, username, roleArray) {
	//console.log(orgDropdown);
	var selOrg = $("#"+orgName).val();	
	$.ajax({
		type: "GET",
		url: '/removeRoles4User',
		data: {
			"orgName": selOrg, "userName": username, "roles" : roleArray.toString()
		},
		success: function(data, textStatus, jqXHR) {
			if (data) {
				//console.log(data);
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.log(errorThrown);
		}
	});
}

function resetAssignRoles4User() {
	$("#searchUser").val("");
	$('.group-available').empty();
	$('.group-assigned').empty();
	$('.group-available').append('<li class="list-group-item list-group-item-danger">No Roles Available</li>');
	$('.group-assigned').append('<li class="list-group-item list-group-item-danger">Please select an user</li>');

}

function resetAssignRoles4Tab() {
	$(".dep-specific-block").hide();
	$('#assignRoleTab-include-department').bootstrapToggle('off');
	$('.tab-group-available').empty();
	$('.tab-group-assigned').empty();
	$('.tab-group-available').append('<li class="list-group-item list-group-item-danger">No Tabs Available</li>');
	$('.tab-group-assigned').append('<li class="list-group-item list-group-item-danger">Please select an organization unit</li>');

}

function initializeAssignRolesUser() {

	resetAssignRoles4User();

	$('.move-arrows').click(function () {
		//console.log("Clicked");
		var currentUser = $("#searchUser");
		if(currentUser.val() != "") {
			var $button = $(this), actives = '';
			if ($button.hasClass('move-left')) {
				var addedRoles = [];
				actives = $('.list-right ul li.active');
				//console.log("Length: "+actives.length);
				if(actives.length <= 0) {
					$(".move-left").notify("Please select atleast a role to continue. R", { position:"top" });
				} else {
					actives.each(function() {
						console.log($(this).attr("class"));
						$(this).removeClass("list-group-item-success");
						$(this).removeClass("active");
						$(this).addClass("list-group-item-primary");
						addedRoles.push($(this).text());
					});

					actives.clone().appendTo('.list-left ul');
					actives.remove();

					addRoles4User("selOrgAssignUserRole", currentUser.val(), addedRoles);
				}		            

			} else if ($button.hasClass('move-right')) {
				var removedRoles = [];
				var listSize = $('.list-left ul li').length;
				if(listSize > 1) {
					actives = $('.list-left ul li.active');
					//console.log("Length: "+actives.length);
					if(actives.length <= 0) {
						$(".move-right").notify("Please select atleast a role to continue. L", { position:"top" });
					} else {
						actives.each(function() {
							//console.log($(this).attr("class"));
							$(this).removeClass("list-group-item-primary");
							$(this).removeClass("active");
							$(this).addClass("list-group-item-success");
							removedRoles.push($(this).text());
						});

						actives.clone().appendTo('.list-right ul');
						actives.remove();

						removeRoles4User("selOrgAssignUserRole", currentUser.val(), removedRoles);
					}
				} else {
					$button.notify("User should have atleast one role.", { position:"top" });
				}
			}
		}	else {
			currentUser.notify("Please select an user and continue.", { position:"bottom" });
		}
	});

	$(document).on("click", "#searchUser", function() {
		var selOrgAssignUserRole = $("#selOrgAssignUserRole");
		$("#searchUser").autocomplete({
			minLength: 3,
			source: function(request, response) {
				$.ajax({
					url: "/searchUsers",
					data: {
						"searchTerm": request.term,
						"orgName": selOrgAssignUserRole.val(),
						"listSize": "5"
					},
					success: function(data) {
						//console.log(data);
						var dataList = data.split("###");
						response(dataList);
					}
				});
			},
			appendTo: "#assignRole4UserModal",
			select: function(event, ui) {
				var selUsername = ui.item.label;
				$(this).data('selected-item', selUsername);
				$(".group-available").hide();
				loadRolesAvailable("selOrgAssignUserRole");
				loadUserRoles("selOrgAssignUserRole", selUsername);
				setTimeout(function() {
					hideAssignedRoles();
				}, 1200);
			}
		}).blur(function() {
			var value = $(this).val();
			//check if the input's value matches the selected item
			if (value != $(this).data('selected-item')) {
				$(this)
				.val('') //clear the input's text
				.data('selected-item', ''); //clear the selected item
				$(this).addClass("error");
			} else {
				$(this).removeClass("error");
			}
		});

	});
}


function initializeAssignRolesTab() {

	resetAssignRoles4Tab();

	$("#selOrgAssignTabRole").change(function() {
		setTimeout(function() {
			loadOrgUnitAlternate($("#selOrgAssignTabRole"), "selOrgUnitAssignTabRole");
		}, 500);
		setTimeout(function() {
			loadDepartmentListAlternate($("#selOrgUnitAssignTabRole"), "selOrgAssignTabRole", "selDepartmentAssignTabRole");
			loadRolesAlternate($("#selOrgAssignTabRole"), "selRoleAssignTabRole");
		}, 1000);
		setTimeout(function() {
			loadAssignedTabs();
			loadAvailableTabs();
		}, 1500);
	});

	$("#selOrgUnitAssignTabRole").change(function() {
		loadDepartmentListAlternate($("#selOrgUnitAssignTabRole"), "selOrgAssignTabRole", "selDepartmentAssignTabRole");
		setTimeout(function() {
			loadAssignedTabs();
			loadAvailableTabs();
		}, 1000);
	});

	$("#selRoleAssignTabRole").change(function() {
		loadAssignedTabs();
		loadAvailableTabs();
	});

	$("#selDepartmentAssignTabRole").change(function() {
		loadAssignedTabs();
		loadAvailableTabs();
	});

	$('#assignRoleTab-include-department').change(function() {
		if($(this).prop('checked')) {
			loadDepartmentListAlternate($("#selOrgUnitAssignTabRole"), "selOrgAssignTabRole", "selDepartmentAssignTabRole");
			$(".dep-specific-block").show();
			setTimeout(function() {
				loadAssignedTabs();
				loadAvailableTabs();
			}, 1000);
		} else {
			$(".dep-specific-block").hide();
		}
	});

	$('input[type=radio][name=tabAssign-ou-specific]').change(function() {
		loadAvailableTabs();
		loadAssignedTabs();
	});
	$('input[type=radio][name=tabAssign-dep-specific]').change(function() {
		loadAvailableTabs();
		loadAssignedTabs();
	});

	setTimeout(function() {
		loadAssignedTabs();
		loadAvailableTabs();
	},1000);

	$('.tab-move-arrows').click(function () {
		var currentRole = $("#selRoleAssignTabRole");
		var $button = $(this), actives = '';
		if ($button.hasClass('tab-move-left')) {
			var addedTabs = [];
			actives = $('.tab-list-right ul li.active');
			//console.log(actives.length);
			if(actives.length <= 0) {
				$(".tab-move-left").notify("Please select atleast a tab to continue.", { position:"top" });
			} else {
				actives.each(function() {
					//console.log($(this).attr("class"));
					$(this).removeClass("list-group-item-success");
					$(this).removeClass("active");
					$(this).addClass("list-group-item-primary");
					addedTabs.push($(this).text());
				});

				actives.clone().appendTo('.tab-list-left ul');
				actives.remove();

				addRoles4Tab("selOrgAssignTabRole", currentRole.val(), addedTabs);
			}		            

		} else if ($button.hasClass('tab-move-right')) {
			var removedTabs = [];
			var listSize = $('.tab-list-left ul li').length;
			if(listSize > 1) {
				actives = $('.tab-list-left ul li.active');
				if(actives.length <= 0) {
					$(".tab-move-right").notify("Please select atleast a tab to continue.", { position:"top" });
				} else {
					actives.each(function() {
						//console.log($(this).attr("class"));
						$(this).removeClass("list-group-item-primary");
						$(this).removeClass("active");
						$(this).addClass("list-group-item-success");
						removedTabs.push($(this).text());
					});

					actives.clone().appendTo('.tab-list-right ul');
					actives.remove();

					removeRoles4Tab("selOrgAssignTabRole", currentRole.val(), removedTabs);
				}
			} else {
				$button.notify("Tab should have atleast one role.", { position:"top" });
			}
		}

	});

}



function loadAssignedTabs() {
	var orgName = $("#selOrgAssignTabRole");
	var orgUnitName = $("#selOrgUnitAssignTabRole");
	var depName = $("#selDepartmentAssignTabRole");
	var depIncludeFlag = $('#assignRoleTab-include-department').prop('checked');
	var role = $("#selRoleAssignTabRole");
	var ou_flag = $("input[name='tabAssign-ou-specific']:checked").attr("id");
	var dep_flag = $("input[name='tabAssign-dep-specific']:checked").attr("id");

	getTabs("assigned", orgName.val(), orgUnitName.val(), depIncludeFlag.toString(), depName.val(), role.val(), ou_flag, dep_flag);
}

function loadAvailableTabs() {
	var orgName = $("#selOrgAssignTabRole");
	var orgUnitName = $("#selOrgUnitAssignTabRole");
	var depName = $("#selDepartmentAssignTabRole");
	var depIncludeFlag = $('#assignRoleTab-include-department').prop('checked');
	var role = $("#selRoleAssignTabRole");
	var ou_flag = $("input[name='tabAssign-ou-specific']:checked").attr("id");
	var dep_flag = $("input[name='tabAssign-dep-specific']:checked").attr("id");

	getTabs("available", orgName.val(), orgUnitName.val(), depIncludeFlag.toString(), depName.val(), role.val(), ou_flag, dep_flag);

}


function addRoles4Tab(orgName, roleName, tabArray) {
	//console.log(orgDropdown);
	var selOrg = $("#"+orgName).val();	
	$.ajax({
		type: "GET",
		url: '/addRoles4Tab',
		data: {
			"orgName": selOrg, "roleName": roleName, "tabs" : tabArray.toString()
		},
		success: function(data, textStatus, jqXHR) {
			if (data) {
				//console.log(data);
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.log(errorThrown);
		}
	});
}

function removeRoles4Tab(orgName, roleName, tabArray) {
	//console.log(orgName + " : " + roleName + " : " + tabArray);
	var selOrg = $("#"+orgName).val();
	$.ajax({
		type: "GET",
		url: '/removeRoles4Tab',
		data: {
			"orgName": selOrg, "roleName": roleName, "tabs" : tabArray.toString()
		},
		success: function(data, textStatus, jqXHR) {
			if (data) {
				//console.log(data);
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.log(errorThrown);
		}
	});
}

function deleteTab(btn, orgDropdownId, tabName) {
	if(confirm("Do you want to really delete this Tab?")) {
		var selOrg = $("#"+orgDropdownId).val();	
		$.ajax({
			type: "GET",
			url: '/deleteTab',
			data: {
				"orgName": selOrg, "tabName": tabName
			},
			success: function(data, textStatus, jqXHR) {
				if (data) {
					$.notify(data, { className: "success", position:"top center" });
					$(btn).closest("tr").remove();
				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				$.notify(data, { position:"top center" });
			}
		});
	}
}

//show the options text box

function showMultipleTypes(typeValue,selectID){
	var valueType= $(typeValue).val();
	if(valueType=="Multiple" ){
		$('#'+selectID).show();
	}else{
		$('#'+selectID).hide();
	}	
}

function saveSurvey(){

	window.surveyData.id = $("#id").val();
	window.surveyData.orgUnitName = $("#editselOrgSurvey").val();
	window.surveyData.orgName = $("#editSelOrgUnitSurvey").val();
	window.surveyData.tabName = $("#editSelTabSurvey").val();
	var startDate=$("#editStartDate").val();
	var endDate=$("#editEndDate").val();
	window.surveyData.startDate =formateDatewithSlash(startDate);
	window.surveyData.endDate = formateDatewithSlash(endDate);
	window.surveyData.surveyName = $("#editSurveyName").val();

	var multipleQuestions = []; 
	$('#editsurveyBody tr').each(function (i, j) {
		if($("#checkbox",j).prop("checked") == true){
			var id=$("#id",j).text();
			var question = $('#question', j).text();
			var type = $('#types', j).text();
			var options=$('#options',j).text();
			multipleQuestions.push({ id: id,question: question, type: type,answers:options });
		}
	});
  
	var questions=JSON.stringify(multipleQuestions);
	window.surveyData.question = multipleQuestions;
	//console.log(Object.assign({},window.surveyData));
	$.ajax({
		type: "PUT",
		dataType: 'json',
		url: '/api/updateSurvey',
		data: JSON.stringify(Object.assign({},window.surveyData)),
		contentType: "application/json; charset=utf-8",	
		success: function(data, textStatus, jqXHR) {
			if (data) {
				//	console.log(data);
				$.notify(data.statusString,{ className: "success", position:"top center" });
				$('#editSurveyModal').modal('toggle');
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$('#view-modal-body').notify(data,{ className: "error", position:"right" });
		}
	});
}

//survey hyper link

function getSurvey(surveyName){
	$("#editselOrgSurvey").val("");
	$.ajax({
		type: "GET",
		url: "/api/getSurveyDetails",
		data: {
			"surveyName": surveyName
		},
		success: function(data, textStatus, jqXHR) {
			if (data) {
				//console.log(data);
				//let json = JSON.parse(JSON.stringify(data));
				window.surveyData = data;
				$("#id").val(data[0].id);
				//$("#editSelOrgUnitSurvey option[value="+data[0].organizationUnitName+",text="+data[0].organizationUnitName+"]").attr('selected', 'selected');
			  	$("#editselOrgSurvey").append(new Option(data[0].organizationName,data[0].organizationUnitName)).prop("disabled",true);
				$("#editSelOrgUnitSurvey").append(new Option(data[0].organizationUnitName, data[0].organizationUnitName)).prop("disabled",true);
			    $("#editSelTabSurvey").append(new Option(data[0].channel, data[0].channel)).prop("disabled",true);
				$("#editStartDate").val(formateDate(data[0].startDate));
				$("#editEndDate").val(formateDate(data[0].endDate));
				$("#editSurveyName").val(data[0].surveyName);
				var html=" ";
				$.each(data[0].surveyDetails,function(key,value){
					 html +='<tr>';
					 html +='<td style="display:none;" id="id">'+ data[0].surveyDetails[key].id+'</td>';
					 html +='<td id="question">'+ data[0].surveyDetails[key].question+ '</td>';
					 html +='<td id="types">'+ data[0].surveyDetails[key].type + '</td>';
					 html +='<td id="options">'+ data[0].surveyDetails[key].answers + '</td>';
					 html +='<td> <input type="checkBox" class="form-check-input" name="checkbox" id="checkbox" checked/></td>';
					 html +='</tr>';
	 			});
				$("#editsurveyBody").html(html);
				$("#option").hide();
				//$("#patch-survey-modal-body").html(data);
			}	
		},
		error: function(jqXHR, textStatus, errorThrown) {      	
			$("#patch-survey-modal-body").html("<h3>" + errorThrown + "</h3>");
		}
	});
}

//survey Result hyper link

function getSurveyResult(channelId){

	$.ajax({
		type: "GET",
		url: "/api/getSurveyResults",
		data:{

			"channelId":channelId
		},
		success:function(data, textStatus,jqXHR){
			if(data){
				//	$("#survey-header").hide();
				let table=$('<div class="container " id="survey-results"><div class="row"><div id="text" class="col-sm-9" ><h2 class="text-center text-capitalize font-weight-bold " style="font-size: 18px;">SurveyResults for Rating Questions</h2></div><div  class="col-sm-3"><button class="btn btn-primary" style="margin-bottom:10px;" onclick="downloadExcel(\''+channelId+'\');">Excel</button><button class="btn btn-primary" style="float: right; margin-bottom:10px;" onclick="downloadCsv(\''+channelId+'\');">CSV</button></div></div><div class="table-responsive"><table id="surveyResutTable" class="table table-striped table-bordered table-sm" cellspacing="0" width="100%"><thead><tr><th class="text-center">Question</th><th class="text-center">Chart</th></tr></thead><tbody></tbody></table></div></div>');
				//	console.log(table);
				$("#view-survey-modal-body").html(table); 
				var tableBody = $('#surveyResutTable');
				tableBody.find("tbody tr").remove();		 
				let id=0;
				let json = JSON.parse(JSON.stringify(data));

				for(let [key,value] of Object.entries(json)){ 
					id=id+1;
					tableBody.append('<tr><td scope="col" id="question-'+id+'"><p class="text-capitalize font-weight-normal" >Q) '+key+'</p></td><td scope="col"><canvas id="chart-'+id+'" height="165" style="height: 165px;"></canvas></td>');
					for(let [keys,values] of Object.entries(value)){
						displayChart("chart-"+id,JSON.parse(keys),values);		
					}
				} 
				var table1 = $('#surveyResutTable').DataTable({
					"pagingType": "full_numbers",
					"scrollY": "255px",
					"scrollCollapse": false,
					"aLengthMenu": [[1, 5, 10, 25, 50, -1], [1, 5, 10, 25, 50, "All"]],
					"iDisplayLength": 1,
					"order": [[ 1, "asc" ]]
				});

			}
		},
		error:function(jqXHR,textStatus,errorThrown){
			$("#view-survey-modal-body").html("<h3>"+errorThrown+"</h3>");
		}
	});
}

function  displayChart(id,keys,values){
	// console.log(id);
	//console.log(keys);
	//console.log(values);	
	var ctx = document.getElementById(id).getContext('2d');
	ctx.imageSmoothingEnabled = true;
	var myChart = new Chart(ctx, {

		plugins: [ChartDataLabels],
		type: 'pie',
		data: {
			labels:keys,
			datasets: [{
				data:values,
				backgroundColor:["#FF6F61","#C62168","#B565A7","#009B77","#D65076","#45B8AC","#EFC050","#5B5EA6","#955251"],
				hoverBackgroundColor: ["#FF6F61","#C62168","#B565A7","#009B77","#D65076","#45B8AC","#EFC050","#5B5EA6","#955251"]
			}]
		}, 
		options: {
			responsive: true,
			legend: {
				position: 'right',
				labels: {
					padding: 20,
					boxWidth: 10
				}
			},
			plugins: {
				datalabels: {
					formatter: (value, ctx) => {
						let sum = 0;
						let dataArr = ctx.chart.data.datasets[0].data;
						dataArr.map(data => {
							sum += data;
						});
						let percentage = (value * 100 / sum).toFixed(1) + "%";
						return percentage;
					},
					color: 'black',
					labels: {
						title: {
							font: {
								size: '14'
							}
						}
					}
				}
			}
		}
	});

}


function downloadExcel(channelId){
	//console.log(channelId);
	$.ajax({
		type: "GET",
		url: "/api/getSurveyResultsWithoutRatingQuestions",
		data: {
			"channelId": channelId
		},
		success: function(data, textStatus, jqXHR) {
			if (data) {
				//console.log(data);
				convertJsonToExcel(data);
			}
			$('#input-options').hide();
		},
		error: function(jqXHR, textStatus, errorThrown) {  	
			$("#view-xl-modal-body").html("<h3>" + errorThrown + "</h3>");
		}
	});
}

function convertJsonToExcel(data){
	const EXCEL_TYPE = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8';
	const EXCEL_EXTENSION = '.xlsx';
	const workSheet=XLSX.utils.json_to_sheet(data);
	const workBook={
			Sheets:{

				'data':workSheet
			},
			SheetNames:['data']
	};
	const excelBuffer=XLSX.write(workBook,{bookType:'xlsx',type:'array'});
	saveAsExcel(excelBuffer,'survey-results',EXCEL_TYPE,EXCEL_EXTENSION);
}

function saveAsExcel(buffer,filename,excelType,fileExtension){
	const data=new Blob([buffer],{type:excelType});
	saveAs(data,filename+'_export_'+new Date().getTime()+fileExtension)
}


function downloadCsv(channelId){
	//console.log(channelId);
	$.ajax({
		type: "GET",
		url: "/api/getSurveyResultsWithoutRatingQuestions",
		data: {
			"channelId": channelId
		},
		success: function(data, textStatus, jqXHR) {
			if (data) {
				//console.log(data);
				JSONToCSVConvertor(data,"Survey Results",true);
			}
			$('#input-options').hide();
		},
		error: function(jqXHR, textStatus, errorThrown) {    	
			$("#view-xl-modal-body").html("<h3>" + errorThrown + "</h3>");
		}
	});
}

function JSONToCSVConvertor(JSONData, ReportTitle, ShowLabel) {
	//If JSONData is not an object then JSON.parse will parse the JSON string in an Object
	var arrData = typeof JSONData != 'object' ? JSON.parse(JSONData) : JSONData;

	var CSV = '';    
	//Set Report title in first row or line

	CSV += ReportTitle + '\r\n\n';

	//This condition will generate the Label/Header
	if (ShowLabel) {
		var row = "";

		//This loop will extract the label from 1st index of on array
		for (var index in arrData[0]) {

			//Now convert each value to string and comma-seprated
			row += index + ',';
		}

		row = row.slice(0, -1);

		//append Label row with line break
		CSV += row + '\r\n';
	}

	//1st loop is to extract each row
	for (var i = 0; i < arrData.length; i++) {
		var row = "";

		//2nd loop will extract each column and convert it in string comma-seprated
		for (var index in arrData[i]) {
			row += '"' + arrData[i][index] + '",';
		}

		row.slice(0, row.length - 1);

		//add a line break after each row
		CSV += row + '\r\n';
	}

	if (CSV == '') {        
		alert("Invalid data");
		return;
	}   

	//Generate a file name
	var fileName = "Report_";
	//this will remove the blank-spaces from the title and replace it with an underscore
	fileName += ReportTitle.replace(/ /g,"_");   

	//Initialize file format you want csv or xls
	var uri = 'data:text/csv;charset=utf-8,' + escape(CSV);

	//this trick will generate a temp <a /> tag
	var link = document.createElement("a");    
	link.href = uri;

	//set the visibility hidden so it will not effect on your web-layout
	link.style = "visibility:hidden";
	link.download = fileName + ".csv";

	//this part will append the anchor tag and remove it after automatic click
	document.body.appendChild(link);
	link.click();
	document.body.removeChild(link);
}

//go to previous page
/*function previousPage(divData){
	$("#survey-results").hide();
	$.ajax({
	        type: "GET",
	        url: '/getSurvey',
	        success: function(data, textStatus, jqXHR) {
	            if (data) {
	            	//console.log(data);
	            	$("#view-xl-modal-body").html(data);
	            }
	        },
	        error: function(jqXHR, textStatus, errorThrown) {
	        	$("#view-xl-modal-body").html("<h3>" + errorThrown + "</h3>");
	        }
	    });
}
 */

//Validation methods

function validateOrganizationName(orgName) {
	$.ajax({
		type: 'GET',
		url: '/validateOrgName',
		data: {
			'orgName': orgName
		},
		success: function(data, textStatus, jqXHR) {
			if (data) {
				//console.log(data);
				return data;
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			console.log(errorThrown);
			return "false";
		}
	}); 
}

function formateDate(inputDate){
	var date=inputDate.split("/");
	var year=date[0];
	var month=date[1];
	var day=date[2];
	return year+"-"+month+"-"+day;
}
function formateDatewithSlash(inputDate){
	var date=inputDate.split("-");
	var year=date[0];
	var month=date[1];
	var day=date[2];
	return year+"/"+month+"/"+day;
}


