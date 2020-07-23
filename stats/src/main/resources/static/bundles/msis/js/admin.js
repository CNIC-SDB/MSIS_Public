/**
 * 常用的插件封装.
 * javaScript language version:ECMAScript 3
 * @author zldc
 * @version 1.5.x-adminlte
 */
(function ($) {
    'use strict';
    //admin核心api
    let _my_admin_api = {
        /**
         * 如果参数是 false，它就会用 HTTP 头 If-Modified-Since 来检测服务器上的文档是否已改变,否则就绕过缓存刷新页面
         * @param forceget 是否绕过缓存
         */
        refresh: function (forceget) {
            window.location.reload(forceget);
        },
        //刷新表格
        /**
         * 刷新远程服务器数据，可以设置{silent: true}以静默方式刷新数据，并设置{url: newUrl}更改URL。 要提供特定于此请求的查询参数，请设置{query: {foo: 'bar'}}。
         */
        refreshTable: function () {
            $('.modal').modal('hide');
            //以静默方式刷新数据
            $('table').bootstrapTable('refresh', {silent: true});
        },
        refreshContent: function () {
            let u = window.location.href;
            let i = u.indexOf('#');
            if (i != -1) {
                let s = u.substr(i);
                if (s) {
                    $('.modal').modal('hide');
                    $('.modal-backdrop').remove();
                    $.myAdmin.loadContent(s);
                }
            }
        },
        /**
         * 加载content区域
         * @param href #锚点
         * @param callback 回调
         */
        loadContent: function (href, callback) {
            if (href) {
                if (typeof String.prototype.startsWith != 'function') {
                    String.prototype.startsWith = function (prefix){
                        return this.slice(0, prefix.length) === prefix;
                    };
                }
                if (href.startsWith('http')) {
                    window.open(href);
                    return;
                } else {
                    //重写url，定位content
                    window.history.pushState('', 0, href);
                }
            }
            let url = window.location.href;
            if (url.indexOf('#') > 0 && url.substr(url.indexOf('#') + 1).length > 0) {
                let s = url.indexOf('#');
                if (url.substr(s - 1, 1) === '/' || url.substr(s + 1, 1) === '/') {
                    url = url.replace('#', '');
                } else {
                    url = url.replace('#', '/');
                }
                msisAPP.layerOpen();

                $('#content-wrapper').load(url, function (response, status, xhr) {
                    if (status == "success") {
                        msisAPP.layerClose();
                    }
                    //重新加载组件
                    reloadComponent();
                    callback && callback();
                });
            }

            let error = function (msg) {
                console.error(msg);
                $.myNotify.danger(msg);
            };
            //jQuery因为删除dom事件也会失效，所以页面需要注册下部分依赖jQuery的组件和事件
            let reloadComponent = function () {
                //自定义事件
                $('[data-action]').on('click', function (e) {
                    let str = $(this).attr('data-action');
                    try {
                        var obj = eval('(' + str + ')');
                    } catch (err) {
                        console.error(err);
                        error('[data-action]有误，请检查语法')
                    }
                    //前置事件
                    if (obj.before) {
                        eval(obj.before + '(obj)');
                    }
                    //前置事件
                    if (obj.before) {
                        eval(obj.before + '(obj)');
                    }
                    if (obj && obj.type) {
                        switch (obj.type) {
                            //提交
                            case 'submit':
                                if (obj.form && obj.url) {
                                    e.preventDefault();
                                    if (/^@{(.*?)}$/g.test(obj.url)) {
                                        obj.url = CONTEXT_PATH + obj.url.substring(2, obj.url.indexOf("}"))
                                    }
                                    $(obj.form).submit({url: obj.url},
                                        function (data) {
                                            if (obj.after) {
                                                eval(obj.after + '(obj,data)');
                                            }
                                        })
                                } else {
                                    error('[data-action]参数有误，请检查参数form和url是否存在');
                                }
                                break;
                            //编辑模式
                            case 'editable':
                                if (obj.form && obj.table) {
                                    let data = $(obj.table).bootstrapTable('getSelections');
                                    if (data.length === 0) {
                                        $.myNotify.warning('请选择一条记录');
                                        return false;
                                    } else if (data.length > 1) {
                                        $.myNotify.warning('最多可选一条记录');
                                        return false;
                                    }
                                    $(obj.form).fillForm(data[0]);
                                    if (obj.after) {
                                        eval(obj.after + '(obj,data[0])');
                                    }
                                    $(obj.form).validator('validate');
                                    //select2
                                    //Initialize Select2 Elements
                                    // $('.select2').select2({width: '100%'})
                                } else {
                                    error('[data-action]参数有误，请检查参数form和table是否存在');
                                }
                                break;
                            //删除模式
                            case 'delete':
                                if (obj.form && obj.table) {
                                    let data = $(obj.table).bootstrapTable('getSelections');
                                    if (data.length === 0) {
                                        $.myNotify.warning('请选择一条记录');
                                        return false;
                                    }
                                    $('.records').html(data.length);
                                    let html = '';
                                    for (let i in data) {
                                        let id = eval('data[i].' + obj.idField);
                                        html += '<input type="hidden" name="' + obj.idField + '" value="' + id + '">';
                                    }
                                    $(obj.form).html(html);

                                } else {
                                    error('[data-action]参数有误，请检查参数form和table是否存在');
                                }
                                break;
                            default :
                                error('[data-action]参数有误，请检查参数type是否有误');
                        }
                    }
                });
                //模态框隐藏的事件
                $('.modal').on('hidden.bs.modal', function (e) {
                    // 清空弹窗数据
                    if ($(this).find('form').size()) {
                        $(this).find('form').clearForm();
                    }
                    // 清空弹窗表格选中
                    $('table').bootstrapTable('uncheckAll');
                });
                //模态框调用事件
                $('.modal').on('show.bs.modal', function (e) {

                })
                //bootstrap-table选择按钮事件
                $('table').on('check.bs.table uncheck.bs.table check-all.bs.table uncheck-all.bs.table', function () {
                    let $table = $(this);
                    $('[data-action]').each(function (i) {
                        let str = $(this).attr('data-action');
                        try {
                            let obj = eval('(' + str + ')');
                        } catch (err) {
                            error('[data-action]有误，请检查语法')
                        }
                        if (obj && obj.type) {
                            switch (obj.type) {
                                case 'editable':
                                    $(this).prop('disabled', !($table.bootstrapTable('getSelections').length === 1));
                                    break;
                                case 'delete':
                                    $(this).prop('disabled', !$table.bootstrapTable('getSelections').length);
                                    break;
                            }
                        }
                        if (obj && obj.selection) {
                            switch (obj.selection) {
                                case 'single':
                                    $(this).prop('disabled', !($table.bootstrapTable('getSelections').length === 1));
                                    break;
                                case 'multi':
                                    $(this).prop('disabled', !$table.bootstrapTable('getSelections').length);
                                    break;
                            }
                        }
                    });
                });
                //表格事件
                $('table').on('all.bs.table', function (e, name, args) {
                    $('[data-toggle="tooltip"]').tooltip();
                    $('[data-toggle="popover"]').popover();

                });
                //初始化表单验证
                // $('form').validator({'disable': false});
                //select2
                //Initialize Select2 Elements
                // $('.select2').select2({width: '100%'})累计页面访问数;
            };
        }

    };
    $.myAdmin = _my_admin_api;

    //[data-action]内置函数
    let _my_action_api = {
        //刷新表格
        refreshTable: function (obj, data) {
            if (data.success) {
                $('.modal').modal('hide');
                //以静默方式刷新数据
                $('table').bootstrapTable('refresh', {silent: true});
            }
        },
        //刷新内容区域
        refreshContent: function (obj, data) {
            if (data.success) {
                let u = location.href;
                let i = u.indexOf('#');
                if (i != -1) {
                    let s = u.substr(i);
                    if (s) {
                        $('.modal').modal('hide');
                        $('.modal-backdrop').remove();
                        $.myAdmin.loadContent(s);
                    }
                }
            }
        },
    };
    $.myAction = _my_action_api;

    // 重置表单
    let _clear = function () {
        $(this).get(0).reset();
    };
    $.fn.clearForm = _clear;

    //填充表单
    let _fill_form = function (row) {
        let $form = $(this);
        $.each(row, function (key, value) {
            // 如果类型为单选框
            if ($form.find('[name="' + key + '"]').attr('type') == 'radio') {
                $form.find('[name="' + key + '"][value="' + value + '"]').prop('checked', true);
            } else if (typeof (value) === "boolean") {
                //布尔类型转换为数值0和1
                $form.find('[name="' + key + '"]').val(value + 0);
            } else {
                $form.find("[name='" + key + "']").val(value);
            }
        })
    };
    $.fn.fillForm = _fill_form;

    //提交
    let _submit = function (opts, callback) {
        let $form = $(this),
            _url = opts.url,
            _data = (opts.data || $form.serialize()),
            _dataType = (opts.dataType || 'json');
        $.ajax({
            type: 'post',
            url: _url,
            dataType: _dataType,
            data: _data,
            success: function (data) {
                if (data.success) {
                    $.myNotify.success(data.msg);
                } else {
                    $.myNotify.warning(data.msg);
                }

                callback && callback(data);
            },
            error: function (data) {
                callback && callback(data);
            }
        });
    };
    $.fn.submit = _submit;

    //提示框
    let _my_notify_api = {
        //成功
        success: function (message) {
            return $.notify({
                // options
                icon: 'glyphicon glyphicon-ok-sign',
                message: message
            }, {
                // settings
                offset: {
                    x: 80,
                    y: 20
                },
                type: 'success'
            });
        },
        //信息
        info: function (message) {
            return $.notify({
                // options
                icon: 'glyphicon glyphicon-info-sign',
                message: message
            }, {
                // settings
                offset: {
                    x: 80,
                    y: 20
                },
                type: 'info'
            });
        },
        //警告
        warning: function (message) {
            return $.notify({
                // options
                icon: 'glyphicon glyphicon-warning-sign',
                message: message
            }, {
                // settings
                offset: {
                    x: 80,
                    y: 20
                },
                type: 'warning'
            });
        },
        //错误
        danger: function (message) {
            return $.notify({
                // options
                icon: 'glyphicon glyphicon-remove-sign',
                message: message
            }, {
                // settings
                offset: {
                    x: 80,
                    y: 20
                },
                type: 'danger'
            });
        }

    };
    $.myNotify = _my_notify_api;

})(jQuery);

//定义一些处理
(function ($) {

    // sidebar绑定事件
    $('li.jump').on('click', function (e) {
        let href = $(this).find('a').attr('href');
        if (href && href != "#") {
            let $groupsList = $("#groupsList option");
            let ajaxUrl = "/integrator/homePage/groupsList";
            let options = "<option value='0'>选择数据库</option>";
            options += "<option value='1'>科学数据库</option>";
            $.ajax({
                url: ajaxUrl,
                type: "POST",
                async: false,
                dataType: "JSON",
                success: function (result) {
                    $.each(result, function (index, value) {
                        options += "<option value='" + value.id + "'>&nbsp;&nbsp;--" + value.name + "</option>"
                    });
                    $groupsList.remove();
                    $("#groupsList").append(options);

                }
            });

            // 加载Content
            $.myAdmin.loadContent(href);
            e.preventDefault();
        }
    });

    //bootstrap-table默认设置
    $.extend($.fn.bootstrapTable.defaults, {
        height: getHeight(),
        contentType: "application/x-www-form-urlencoded",
        striped: true,
        search: false,
        showToggle: false,
        minimumCountColumns: 2,
        clickToSelect: true,
        detailView: false,
        detailFormatter: 'detailFormatter',
        classes: 'table table-hover table-no-bordered',
        sidePagination: 'server',
        silentSort: false,
        // 禁用分页条无限循环的功能。
        paginationLoop: false,
        // 显示分页插件
        pagination: false,
        pageNumber: 1,
        pageSize: 20,
        // 禁用自定义分页参数
        pageList: [],
        // queryParams 返回值 pageSize, pageNumber, searchText, sortName, sortOrder.返回false将会终止请求。
        queryParamsType: "",
        // 表头样式设置
        theadClasses: "thead-dark",
        // 转义HTML字符串，替换 &, <, >, ", \`, 和 ' 字符。
        escape: true,
        // 记住checkbox选项
        maintainSelected: true,
    });

    // 动态高度
    function getHeight() {
        return $('.bootstrap-table').height();
    }

    // 数据表格动态高度
    $(window).resize(function () {
        $('#table').bootstrapTable('resetView', {
            height: getHeight()
        });
    });

    // jquery-validate插件增加自定义验证方法
    // 验证手机号
    jQuery.validator.addMethod("isPhoneNum", function (value, element) {
        var length = value.length;
        var mobile = /^1[0-9]{1}[0-9]{9}$/;
        return this.optional(element) || (length === 11 && mobile.test(value));
    }, "请填写正确的手机号码");
    // 验证空白字符
    jQuery.validator.addMethod("isNoBlank", function (value, element) {
        var length = $.trim(value).length;
        return length !== 0;
    }, "请不要填写空白字符");
    jQuery.validator.addMethod("isPositive", function (value, element) {
        return /^[1-9]\d*$/.test(value) || /^[1-9]\d*\.\d*|0\.\d*[1-9]\d*$/.test(value);
    });
    // 验证站点属性的唯一性
    jQuery.validator.addMethod("uniqueSiteUri", function (value, element) {
        let result = false;
        let data = {
            "uri": value,
            "id": $(element.form).find("[name='id']").val()
        };
        $.ajax({
            url: "/manage/site/validateUriOrEnName",
            type: "POST",
            data: data,
            async: false,
            dataType: "JSON",
            success: function (iter) {
                result = Boolean(iter);
            }
        });
        return result;
    });
    jQuery.validator.addMethod("uniqueSiteCnName", function (value, element) {
        let result = false;
        let data = {
            "cnName": value,
            "id": $(element.form).find("[name='id']").val()
        };
        $.ajax({
            url: "/manage/site/validateUriOrEnName",
            type: "POST",
            data: data,
            async: false,
            dataType: "JSON",
            success: function (iter) {
                result = Boolean(iter);
            }
        });
        return result;
    });
    // 加载Content
    $.myAdmin.loadContent();

    $.ajaxSetup({
        // contentType:"application/x-www-form-urlencoded;charset=utf-8",
        complete: function (XMLHttpRequest, textStatus) {
            //通过XMLHttpRequest取得响应头，sessionstatus，
            var sessionstatus = XMLHttpRequest.getResponseHeader("session-status");
            if (sessionstatus === "timeout") {
                //如果超时就处理 ，指定要跳转的页面(比如登陆页)
                window.location.replace("/manage");
            }
        }
    });

        String.prototype.startWith = function (str) {
            var reg = new RegExp("^" + str);
            return reg.test(this);
        }
})(jQuery);