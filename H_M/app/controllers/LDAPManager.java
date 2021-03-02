package controllers;

import java.util.ArrayList;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import play.data.FormFactory;
import play.data.Form;
import play.data.DynamicForm;

import com.typesafe.config.Config;
import javax.inject.Inject;

import play.libs.Json;
import static play.libs.Json.toJson;

import play.mvc.*;
import play.libs.ws.*;
import models.LDAPServiceManager;
import models.dto.DepartmentDto;
import models.dto.NewsDto;
import models.dto.OrganizationDto;
import models.dto.OrganizationUnitDto;
import models.dto.ResponseDto;
import models.dto.RoleDto;
import models.dto.TabDto;
import models.dto.TabTemplateDto;
import models.dto.UserDto;
import models.entity.SurveyResult;
import models.ldap.*;


public class LDAPManager  extends Controller {

	private final Config config;
	LDAPServiceManager ldapServiceManager;

	ResponseDto responseDto=null;

	@Inject
	public LDAPManager(Config config) {
		this.config = config;
		ldapServiceManager = new LDAPServiceManager(this.config);
	}

	public Result createOrganization() {

		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null && loggedUserOrg.equals("")) {
			String response = "";
			Http.Request request = request();
			JsonNode json = request.body().asJson();
			OrganizationDto organizationDto=Json.fromJson(json,OrganizationDto.class);

			try {
				ldapServiceManager.createOrganization(organizationDto.getOrgName(), organizationDto.getOrgSupportEmail(), organizationDto.getOrgSupportContactNumber());
				response = "Organization creation successful.";
				responseDto=new  ResponseDto();
				responseDto.setStatusString(response);
				responseDto.setData(organizationDto);
				return ok(toJson(responseDto));
			}
			catch(Exception exp) {
				exp.printStackTrace();
				response = "Organization creation failed.";
				return badRequest(toJson(response));
			}
		} else {

			return unauthorized("Unauthorized");
		}

	}

	public Result getOrganizationList(String displayFlag) {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
			String response = "";
			ArrayList<String> orgList = new ArrayList<String>();

			try {
				orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
				if(displayFlag.equals("page")) {
					return ok(toJson(orgList));    
				} else {
					String orgListString = String.join(",", orgList);
					return ok(toJson(orgListString));    
				}            
			}
			catch(Exception exp) {
				exp.printStackTrace();
				response = "Error occurred while reading the data.";
				return badRequest(toJson(response));
			}
		} else {

			return unauthorized("Unauthorized");
		}      
	}

	public Result createOrganizationUnit() {

		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
			String response = "";
			Http.Request request = request();
			JsonNode json = request.body().asJson();
			OrganizationUnitDto organizationUnitDto = Json.fromJson(json, OrganizationUnitDto.class);
			try {
				ldapServiceManager.createOrganizationUnit(organizationUnitDto.getOrgUnitName(),organizationUnitDto.getOrgName(), organizationUnitDto.getOrgUnitLocation(), organizationUnitDto.getOrgUnitLatitude(), organizationUnitDto.getOrgUnitLongitude(), organizationUnitDto.getOrgUnitPhone());
				response = "Organization Unit creation successful.";
				responseDto=new  ResponseDto();
				responseDto.setStatusString(response);
				responseDto.setData(organizationUnitDto);
				return ok(toJson(responseDto));
			}
			catch(Exception exp) {
				exp.printStackTrace();
				response = "Organization Unit creation failed.";
				return badRequest(toJson(response));
			}
		} else {

			return unauthorized("Unauthorized");
		}
	}

	public Result getOrganizationUnitList(String  displayFlag,String orgName) {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
			String response = "";
			ArrayList<String> orgUnitList = new ArrayList<String>();
			ArrayList<String> orgList = new ArrayList<String>();
			try {
				orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
				if(orgName.equals("")) {
					if(orgList.size()>0) {
						orgUnitList = ldapServiceManager.getOrganizationUnitList(orgList.get(0));    
					} 
				} else {
					orgUnitList = ldapServiceManager.getOrganizationUnitList(orgName);    
				}               

				if(displayFlag.equals("page")) {
					return ok(views.html.viewPages.viewOrganizationUnitList.render(orgList, orgUnitList));    
				} else {
					//orgUnitList = ldapServiceManager.getOrganizationUnitList(orgName); 
					String orgUnitListString = String.join(",", orgUnitList);
					return ok(toJson(orgUnitListString));
				}            
			}
			catch(Exception exp) {
				exp.printStackTrace();
				response = "Error occurred while reading the data.";
				return badRequest(toJson(response));
			}
		} else {

			return unauthorized("Unauthorized");
		}


	}

	public Result createDepartment() {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
			String response = "";
			Http.Request request = request();
			JsonNode json = request.body().asJson();
			DepartmentDto departmentDto=Json.fromJson(json,DepartmentDto.class);
			try {
				ldapServiceManager.createDepartment(departmentDto.getDepartmentName(), departmentDto.getOrgUnitName(), departmentDto.getOrgName());
				response = "Department creation successful.";
				responseDto=new  ResponseDto();
				responseDto.setStatusString(response);
				responseDto.setData(departmentDto);
				return ok(toJson(responseDto));
			}
			catch(Exception exp) {
				exp.printStackTrace();
				response = "Department creation failed.";
				return badRequest(toJson(response));
			}
		} else {

			return unauthorized("Unauthorized");
		}
	}

	public Result getDepartmentList(String displayFlag,String orgName,String orgUnitName) {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
			String response = "";
			ArrayList<String> orgUnitList = new ArrayList<String>();
			ArrayList<String> orgList = new ArrayList<String>();
			ArrayList<String> departmentList = new ArrayList<String>();


			try {
				orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
				if(!orgName.equals("") && !orgUnitName.equals("")) {
					departmentList = ldapServiceManager.getDepartmentList(orgName, orgUnitName);
				} else {
					String oName = orgList.get(0);
					orgUnitList = ldapServiceManager.getOrganizationUnitList(oName);
					departmentList = ldapServiceManager.getDepartmentList(oName, orgUnitList.get(0));   
				}

				if(displayFlag.equals("page")) {
					return ok(views.html.viewPages.viewDepartmentList.render(orgList, orgUnitList, departmentList));    
				} else {
					String departmentListString = String.join(",", departmentList);
					return ok(toJson(departmentListString));
				}            
			}
			catch(Exception exp) {
				exp.printStackTrace();
				response = "Error occurred while reading the data.";
				return badRequest(toJson(response));
			}
		} else {

			return unauthorized("Unauthorized");
		}


	}

	public Result createRole() {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
			String response = "";
			Http.Request request = request();
			JsonNode json = request.body().asJson();
			RoleDto roleDto=Json.fromJson(json,RoleDto.class);

			try {
				ldapServiceManager.createRole(roleDto.getRoleName(),roleDto.getRoleType(),roleDto.getOrgName(),roleDto.getAccessInfo(), roleDto.getOrgUnitName(), roleDto.getDepName());
				response = "Role creation successful.";
				responseDto=new  ResponseDto();
				responseDto.setStatusString(response);
				responseDto.setData(roleDto);
				return ok(toJson(responseDto));
			}
			catch(Exception exp) {
				exp.printStackTrace();
				response = "Role creation failed.";
				return badRequest(toJson(response));
			}
		} else {

			return unauthorized("Unauthorized");
		}
	}

	public Result getRoleList(String displayFlag,String orgName) {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
			String response = "";

			ArrayList<String> orgList = new ArrayList<String>();
			ArrayList<String> roleList = new ArrayList<String>();
			ArrayList<Role> roles = new ArrayList<Role>();

			try {
				orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
				if(orgName.equals("")) {
					if(orgList.size()>0) {
						roleList = ldapServiceManager.getRoleList(orgList.get(0), "page");
						roles = ldapServiceManager.getRoles(orgList.get(0));   
					} 
				} else {
					roleList = ldapServiceManager.getRoleList(orgName, "page");
					roles = ldapServiceManager.getRoles(orgName);
				}               

				if(displayFlag.equals("page")) {
					return ok(views.html.viewPages.viewRoleList.render(orgList, roleList, roles));    
				} else {
					String roleListString = String.join(",", roleList);
					return ok(toJson(roleListString));
				}           
			}
			catch(Exception exp) {
				exp.printStackTrace();
				response = "Error occurred while reading the data.";
				return badRequest(toJson(response));
			}
		} else {

			return unauthorized("Unauthorized");
		}               
	}

	public Result createUser() {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
			String response = "";
			Http.Request request = request();
			JsonNode json = request.body().asJson();
			UserDto  userDto=Json.fromJson(json,UserDto.class);

			try {
				ldapServiceManager.createUser(userDto.getFirstName(),userDto.getLastName(),userDto.getUsername(),userDto.getPassword(),userDto.getEmail(),
						userDto.getQualification(),userDto.getSpecialization(),userDto.getEmployeeId(),userDto.getAadhaarNumber(),
						userDto.getPanNumber(),userDto.getPassportNumber(),userDto.getPhoneNumber(),userDto.getOrganizationName(),userDto.getOrganizationUnitName(),
						userDto.getRoleName(),userDto.getDepartmentName(),userDto.getGender(),userDto.getAccessInfo(),userDto.getUniversalAccessInfo());
				response = "User creation successful.";
				responseDto=new  ResponseDto();
				responseDto.setStatusString(response);
				responseDto.setData(userDto);
				return ok(toJson(responseDto));
			}
			catch(Exception exp) {
				exp.printStackTrace();
				response = "User creation failed.";
				return badRequest(toJson(response));
			}
		} else {

			return unauthorized("Unauthorized");
		}                    
	}

	public Result getUserList(String displayFlag,String orgName) {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
			String response = "";

			ArrayList<User> userList = new ArrayList<User>();
			ArrayList<String> orgList = new ArrayList<String>();

			try {
				orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
				if(orgName.equals("")) {
					if(orgList.size()>0) {
						userList = ldapServiceManager.getUsers(orgList.get(0));    
					} 
				} else {
					userList = ldapServiceManager.getUsers(orgName);
				}               

				if(displayFlag.equals("page")) {
					return ok(views.html.viewPages.viewUserList.render(orgList, userList));    
				} 
			}
			catch(Exception exp) {
				exp.printStackTrace();
				response = "Error occurred while reading the data.";
				return badRequest(toJson(response));
			}
			response = "Error occurred while reading the data.";
			return badRequest(toJson(response));
		} else {

			return unauthorized("Unauthorized");
		}
	}

	public Result createTabTemplate() {

		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
			String response = "";
			Http.Request request = request();
			JsonNode json = request.body().asJson();
			TabTemplateDto tabTemplateDto=Json.fromJson(json,TabTemplateDto.class);

			try {
				ldapServiceManager.createTabtemplate(tabTemplateDto.getTabTemplateName(), tabTemplateDto.getOrgName());
				response = "Tab Template creation successful.";
				responseDto=new  ResponseDto();
				responseDto.setStatusString(response);
				responseDto.setData(tabTemplateDto);
				return ok(toJson(responseDto));
			}
			catch(Exception exp) {
				exp.printStackTrace();
				response = "Tab Template creation failed.";
				return badRequest(toJson(response));
			}
		} else {

			return unauthorized("Unauthorized");
		}       
	}


	public Result getTabTemplateList(String displayFlag,String orgName) {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
			String response = "";

			ArrayList<String> orgList = new ArrayList<String>();
			ArrayList<TabTemplate> tabTemplateList = new ArrayList<TabTemplate>();
			try {
				orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
				if(orgName.equals("")) {
					if(orgList.size()>0) {
						tabTemplateList = ldapServiceManager.getTabTemplates(orgList.get(0));    
					} 
				} else {
					tabTemplateList = ldapServiceManager.getTabTemplates(orgName);    
				}               

				/*if(displayFlag.equals("page")) {
	                    return ok(views.html.viewPages.viewRoleList.render(orgList, roleList));    
	                } else {*/
				//} 

				ArrayList<String> templateNameList = new ArrayList<String>();
				for(TabTemplate tabTemplate: tabTemplateList) {
					templateNameList.add(tabTemplate.getTemplateName());
				}
				String templateNameString = String.join(",", templateNameList);
				return ok(toJson(templateNameString));

			}
			catch(Exception exp) {
				exp.printStackTrace();
				response = "Error occurred while reading the data.";
				return badRequest(toJson(response));
			}
		} else {

			return unauthorized("Unauthorized");
		}


	}

	public Result createTab() {

		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {

			String response = "";
			Http.Request request = request();
			JsonNode json = request.body().asJson();
			TabDto tabDto=Json.fromJson(json,TabDto.class);
			String accessInfo=String.valueOf(tabDto.isAccessAcrossUnits());
			String accessInfoDepartment=String.valueOf(tabDto.isAccessAcrossDepartments());
			try {
				ldapServiceManager.createTab(tabDto.getChannelName(),tabDto.getHeader(),tabDto.getPurpose(),tabDto.getTemplateName(),tabDto.getOrganizationName(),
						accessInfo, tabDto.getOrganizationUnitName(),tabDto.getDepartmentName(),tabDto.getRole(),accessInfoDepartment);
				response = "Tab creation successful.";
				responseDto=new  ResponseDto();
				responseDto.setStatusString(response);
				responseDto.setData(tabDto);
				return ok(toJson(responseDto));
			}
			catch(Exception exp) {
				exp.printStackTrace();
				response = "Tab creation failed.";
				return badRequest(toJson(response));
			}
		} else {

			return unauthorized("Unauthorized");
		}
	}

	public Result getTabList(String displayFlag,String orgName) {

		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
			String response = "";

			ArrayList<Tab> tabList = new ArrayList<Tab>();
			ArrayList<String> orgList = new ArrayList<String>();

			try {
				orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
				if(orgName.equals("")) {
					if(orgList.size()>0) {
						tabList = ldapServiceManager.getTabs(orgList.get(0));    
					} 
				} else {
					tabList = ldapServiceManager.getTabs(orgName);
				}               

				if(displayFlag.equals("page")) {
					return ok(views.html.viewPages.viewTabList.render(orgList, tabList));    
				}     
			}
			catch(Exception exp) {
				exp.printStackTrace();
				response = "Error occurred while reading the data.";
				return badRequest(toJson(response));
			}
			response = "Error occurred while reading the data.";
			return badRequest(toJson(response));
		} else {

			return unauthorized("Unauthorized");
		}                  
	}

	public Result searchUsers(String searchTearm,String organizationName,String listSizes) {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
			String searchTerm = searchTearm;
			String orgName = "";
			String listSize = "";
			try {
				orgName =organizationName;
			}
			catch(Exception exp) {
				//exp.printStackTrace();
				System.out.println("Disease category not found.");
				try {
					orgName = ldapServiceManager.getOrganizationList(loggedUserOrg).get(0);
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			try {
				listSize = listSizes;
				int listSiz = Integer.parseInt(listSize);
			}
			catch(Exception exp) {
				//exp.printStackTrace();
				System.out.println("Invalid number for list size / List size not found.");
				listSize = "5";
			}

			String resultString = "";
			try{
				resultString = ldapServiceManager.searchUsers(orgName, searchTerm, listSize);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return ok(toJson(resultString));
		} else {

			return unauthorized("Unauthorized");
		}

	}

	public Result getTabs() {

        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");

        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            String response = "";
            
            ArrayList<Tab> tabList = new ArrayList<Tab>();
            ArrayList<String> orgList = new ArrayList<String>();

            Map<String, String[]> queryParameters = request().queryString();
            String purpose = queryParameters.get("purpose")[0];
            String orgName = queryParameters.get("orgName")[0];
            String orgUnit = queryParameters.get("orgUnit")[0];
            String includeDepFlag = queryParameters.get("includeDepFlag")[0];
            String department = queryParameters.get("department")[0];
            String role = queryParameters.get("role")[0];
            String has_all_ou_access = queryParameters.get("has_all_ou_access")[0];
            String has_all_dep_access = queryParameters.get("has_all_dep_access")[0];

            try {
                orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
                if(orgName.equals("")) {
                   if(orgList.size()>0) {
                        orgName = orgList.get(0);
                    } 
                }

                if(purpose.equals("assigned")){
                    tabList = ldapServiceManager.getAssignedTabs(orgName, orgUnit, includeDepFlag, department, role);
                    return ok(views.html.viewPages.viewAssignedTabs.render(tabList));
                }
                else{
                    tabList = ldapServiceManager.getAvailableTabs(orgName, orgUnit, includeDepFlag, department, role, has_all_ou_access, has_all_dep_access);
                    return ok(views.html.viewPages.viewAvailableTabs.render(tabList));
                }

                
            }
            catch(Exception exp) {
                exp.printStackTrace();
                response = "Error occurred while reading the data.";
                return badRequest(toJson(response));
            }
            /*response = "Error occurred while reading the data.";
            return badRequest(response);*/
        } else {
            
            return unauthorized("Unauthorized");
        }

    }
  
       // get Tabs based on the OU
    
    public Result getTabsBasedOnOU(String orgName,String orgUnit,String has_all_ou_access,String displayFlag,String tabType ) {
        String loggedUser = ctx().session().get("loggedUser");
        String loggedUserRole = ctx().session().get("loggedUserRole");
        String loggedUserOrg = ctx().session().get("loggedUserOrg");
       
        if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
            String response = "";
            
            ArrayList<String> tabList = new ArrayList<String>();
            ArrayList<String> orgList = new ArrayList<String>();
           
            try {
                orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
                if(orgName.equals("")) {
                   if(orgList.size()>0) {
                        orgName = orgList.get(0);
                    } 
                }
                  tabList = ldapServiceManager.getAvailableTabsBasedOnOU(orgName, orgUnit, has_all_ou_access,tabType);
                  if(displayFlag.equals("page")) {
                      return ok(views.html.viewPages.viewTabsBasedOnOU.render(tabList));    
                  } else {
                      String orgtabListString = String.join(",", tabList);
                      return ok(toJson(orgtabListString));
                  } 
                   
            }catch(Exception exp) {
                exp.printStackTrace();
                response = "Error occurred while reading the data.";
                return badRequest(response);
            }
        } else {
            
            return unauthorized("Unauthorized");
        }

    }

	public Result getUserRoles(String displayFlag,String orgName,String userName) {
	
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
			String response = "";

			ArrayList<String> orgList = new ArrayList<String>();
			String roles = "";

			try {
			
				orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
				if(orgName.equals("")) {
					if(orgList.size()>0) {
					
						roles = ldapServiceManager.getUserRoleList(orgList.get(0), userName, displayFlag);    
					} 
				} else {
					
					roles = ldapServiceManager.getUserRoleList(orgName, userName, displayFlag);
				
				}               

				return ok(toJson(roles));

			}
			catch(Exception exp) {
				exp.printStackTrace();
				response = "Error occurred while reading the data.";
				return badRequest(toJson(response));
			}
		} else {

			return unauthorized("Unauthorized");
		}      
	}

	public Result addRoles4User(String orgName,String userName,String roles) {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
			String response = "";

			ArrayList<String> orgList = new ArrayList<String>();
			try {
				orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
				if(orgName.equals("")) {
					if(orgList.size()>0) {
						response = ldapServiceManager.addRoles4User(orgList.get(0), userName, roles);    
					} 
				} else {
					response = ldapServiceManager.addRoles4User(orgName, userName, roles);
				}

				return ok(toJson(response));
			}
			catch(Exception exp) {
				exp.printStackTrace();
				response = "Error occurred while reading the data.";
				return badRequest(toJson(response));
			}
		} else {

			return unauthorized("Unauthorized");
		}                    
	}


	public Result removeRoles4User(String orgName,String userName,String roles) {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
			String response = "";

			ArrayList<String> orgList = new ArrayList<String>();
			try {
				orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
				if(orgName.equals("")) {
					if(orgList.size()>0) {
						response = ldapServiceManager.removeRoles4User(orgList.get(0), userName, roles);    
					} 
				} else {
					response = ldapServiceManager.removeRoles4User(orgName, userName, roles);
				}

				return ok(toJson(response));
			}
			catch(Exception exp) {
				exp.printStackTrace();
				response = "Error occurred while reading the data.";
				return badRequest(toJson(response));
			}
		} else {

			return unauthorized("Unauthorized");
		}
	}

	public Result deleteUser(String orgName,String userName) {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
			String response = "";

			ArrayList<String> orgList = new ArrayList<String>();

			try {
				orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
				if(orgName.equals("")) {
					if(orgList.size()>0) {
						response = ldapServiceManager.deleteUser(orgList.get(0), userName);    
					} 
				} else {
					response = ldapServiceManager.deleteUser(orgName, userName);
				}

				return ok(toJson(response));
			}
			catch(Exception exp) {
				exp.printStackTrace();
				response = "Error occurred while reading the data.";
				return badRequest(toJson(response));
			}
		} else {

			return unauthorized("Unauthorized");
		}

	}

	public Result addRoles4Tab(String orgName,String roleName,String tabs) {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
			String response = "";

			ArrayList<String> orgList = new ArrayList<String>();

			try {
				orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
				if(orgName.equals("")) {
					if(orgList.size()>0) {
						response = ldapServiceManager.addRoles4Tab(orgList.get(0), roleName, tabs);    
					} 
				} else {
					response = ldapServiceManager.addRoles4Tab(orgName, roleName, tabs);
				}

				return ok(toJson(response));
			}
			catch(Exception exp) {
				exp.printStackTrace();
				response = "Error occurred while reading the data.";
				return badRequest(toJson(response));
			}
		} else {

			return unauthorized("Unauthorized");
		}
	}

	public Result removeRoles4Tab(String orgName,String roleName,String tabs) {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
			String response = "";

			ArrayList<String> orgList = new ArrayList<String>();

			try {
				orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
				if(orgName.equals("")) {
					if(orgList.size()>0) {
						response = ldapServiceManager.removeRoles4Tab(orgList.get(0), roleName, tabs);    
					} 
				} else {
					response = ldapServiceManager.removeRoles4Tab(orgName, roleName, tabs);
				}

				return ok(toJson(response));
			}
			catch(Exception exp) {
				exp.printStackTrace();
				response = "Error occurred while reading the data.";
				return badRequest(toJson(response));
			}
		} else {

			return unauthorized("Unauthorized");
		}
	}

	public Result deleteTab(String orgName,String tabName) {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
			String response = "";

			ArrayList<String> orgList = new ArrayList<String>();
			try {
				orgList = ldapServiceManager.getOrganizationList(loggedUserOrg);
				if(orgName.equals("")) {
					if(orgList.size()>0) {
						response = ldapServiceManager.deleteTab(orgList.get(0), tabName);    
					} 
				} else {
					response = ldapServiceManager.deleteTab(orgName, tabName);
				}

				return ok(toJson(response));
			}
			catch(Exception exp) {
				exp.printStackTrace();
				response = "Error occurred while reading the data.";
				return badRequest(toJson(response));
			}
		} else {

			return unauthorized("Unauthorized");
		}           
	}

	public Result getAuthorizedTabs(String userOrganization,String userName, String userId, String userAuthToken) {
		String responseString = "";
		String statusCode = "";
		try {
			statusCode = ldapServiceManager.validateUser(userOrganization, userName, userId, userAuthToken);
		}
		catch(Exception exp) {
			exp.printStackTrace();
			statusCode = "400";
		}

		if(statusCode.contains("201")) {
			responseString = statusCode.split(":::")[1];
		} else {
			responseString = "Invalid Request";
		}

		return ok(toJson(responseString));
	}

	public Result getChannelMembers(String userName,String userOrganization, String userId,String channelId,String userAuthToken) {

		String responseString = "";
		String statusCode = "";

		try {
			statusCode = ldapServiceManager.getMembers(userOrganization, userName, userId, userAuthToken, channelId);
		}
		catch(Exception exp) {
			exp.printStackTrace();
			statusCode = "400";
		}

		if(statusCode.contains("201")) {
			try {
				responseString = statusCode.split(":::")[1];
			}
			catch(Exception exp) {
				responseString = "Invalid Request";
			}
		} else {
			responseString = "Invalid Request";
		}

		return ok(toJson(responseString));
	}

	public Result createNews() {

		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");


		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null && loggedUserOrg.equals("")) {

			String response = "";
			Http.Request request = request();
			JsonNode json = request.body().asJson();
			NewsDto  newsDto =Json.fromJson(json,NewsDto.class);

			try { 
				String resp=ldapServiceManager.createNews(newsDto.getContent(),newsDto.getOrgName(), newsDto.getOrgUnitName(),newsDto.getTabName());
				responseDto=new  ResponseDto();
				responseDto.setStatusString(resp);
				responseDto.setData(newsDto);
				return ok(toJson(responseDto));  
			}catch(Exception exp) {
				exp.printStackTrace();
				response ="news creation failed."; 
				return badRequest(toJson(response));
			}

		} else {

			return unauthorized("Unauthorized");
		}

	}

	// get user 
	public Result getUser(String userName,String orgName) {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
			  UserDto userDto=null;
			  String response=null;
			try {
				userDto=ldapServiceManager.getUser(userName,orgName);
				return ok(toJson(userDto));
			}
			catch(Exception exp) {
				exp.printStackTrace();
				response = "Error occurred while reading the data.";
				return badRequest(toJson(response));
			}
		} else {

			return unauthorized("Unauthorized");
		}    
	}
	
	//update user in mm & ldap
	public Result updateUser() {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
			String response = "";
			Http.Request request = request();
			JsonNode json = request.body().asJson();
			System.out.println("Input Data");
			System.out.println(json);
			UserDto userDto =Json.fromJson(json,UserDto.class);
			try {
				ldapServiceManager.updateUser(userDto);
				response="User Data Updated Succesfully";
				responseDto=new  ResponseDto();
				responseDto.setStatusString(response);
				responseDto.setData(userDto);
				return ok(toJson(responseDto));  
			}
			catch(Exception exp) {
				exp.printStackTrace();
				response = "Error Occurred";
				return badRequest(toJson(response));
			}
		} else {

			return unauthorized("Unauthorized");
		}    
	}

	
	// get single tab
	public Result getTab(String tabName,String orgName){
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
			 TabDto tabDto=null;
			  String response=null;
			try {
				tabDto=ldapServiceManager.getTab(tabName,orgName);
				return ok(toJson(tabDto));
			}
			catch(Exception exp) {
				exp.printStackTrace();
				response = "Error occurred while reading the data.";
				return badRequest(toJson(response));
			}
		} else {

			return unauthorized("Unauthorized");
		}    
	}
	
	
	//update user in mm & ldap
	public Result updateTab() {
		String loggedUser = ctx().session().get("loggedUser");
		String loggedUserRole = ctx().session().get("loggedUserRole");
		String loggedUserOrg = ctx().session().get("loggedUserOrg");

		if(loggedUser != null && loggedUserRole != null && loggedUserOrg != null) {
			String response = "";
			Http.Request request = request();
			JsonNode json = request.body().asJson();
			TabDto tabDto =Json.fromJson(json,TabDto.class);
			try {
				ldapServiceManager.updateTab(tabDto);
				response="Tab Data Updated Succesfully";
				responseDto=new  ResponseDto();
				responseDto.setStatusString(response);
				responseDto.setData(tabDto);
				return ok(toJson(responseDto));  
			}catch(Exception exp) {
				exp.printStackTrace();
				response = "Error Occurred";
				return badRequest(toJson(response));
			}
		} else {
			return unauthorized("Unauthorized");
			}    
		}

}