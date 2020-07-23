#!/bin/sh
rootPath="${1}"
ftpUser="${2}"
cd ${rootPath}
mkdir ${ftpUser}
#设置ftp根路径文件 所有者、所在组
chown sysuser:sysuser ${rootPath}/${ftpUser}
chmod 766 ${rootPath}/${ftpUser}
#配置ftp账户
echo -e "local_root=${rootPath}/${ftpUser}\nanon_upload_enable=YES\nanon_mkdir_write_enable=YES\nanon_other_write_enable=YES" > /etc/vsftpd/vusers_config/${ftpUser}
