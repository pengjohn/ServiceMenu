#===========================================================================
#
#                        EDIT HISTORY FOR MODULE
#
#This section contains comments describing changes made to the module.
#Notice that changes are listed in reverse chronological order.
#
#when      who            what, where, why
#--------  ------         ------------------------------------------------------
#20110901  PengZhiXiong   Initial .
#20120517  daiyuanqin     add gozoneFmjarlib for autoFm test 
#===========================================================================*/

LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_MODULE_TAGS := optional

LOCAL_PACKAGE_NAME := ServiceMenu
LOCAL_CERTIFICATE := platform
#Gozone start BID xxx zhangjinguo: add for IMEI operate
LOCAL_JAVA_LIBRARIES := qcnvitems qcrilhook

include $(BUILD_PACKAGE)
include $(call all-makefiles-under,$(LOCAL_PATH))
