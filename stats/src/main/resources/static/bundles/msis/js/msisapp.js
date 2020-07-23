// 自定义方法
function GetQueryString(name) {
    let reg = new RegExp("groupID=([^&]*)");
    let r = window.location.href.substring(1).match(reg);

    let site = new RegExp("siteId=([^&]*)");
    let siteId = window.location.href.substring(1).match(site);

    let unit = new RegExp("unitId=([^&]*)");
    let unitId = window.location.href.substring(1).match(unit);

    if (name == "from") {
        if (r != null) {
            return (r.toString().substring(5, 11));
        }
    } else if (name == "siteID") {
        if (siteId != null) {
            var resultSiteID = siteId.toString().split(",");
            return (resultSiteID[1]);
        }
    } else if (name == "unitID") {
        if (unitId != null) {
            var resultUnitID = unitId.toString().split(",");
            return (resultUnitID[1]);
        }
    } else {
        if (r != null) {
            var result = r.toString().split(",");
            return (result[1]);
        }
    }
}
msisAPP = {
    // 标签增加 number-{symbol} 实现标签中数字按${symbol}进位且按千分位格式显示
    // 必须为整数 且为byte
    numberFormat: function () {
        let $number = $("[class*='number-']");
        let regType = /(?=number\-)\S+\b/g;
        $.each($number, function (index, iter) {
            // 获取标记元素
            let attributes = iter.attributes["class"];
            // 获取需要转化的单位
            let symbol = attributes.value.match(regType)[0].split("-")[1];
            if (symbol !== "") {
                // 获取原始值
                let flag = iter.innerText;
                let number = iter.innerText;
                // 按单位转换值
                number = msisAPP.evolveNumber(number, symbol);
                // 千分位显示
                number = msisAPP.thousands(number);
                if (msisAPP.objIsNumber(flag)) {
                    //判断单位
                    symbol = /thousands/i.test(symbol) ? "" : symbol;
                } else {
                    symbol = "";
                }
                // 完成数值的格式化
                iter.innerText = number + symbol.toLocaleUpperCase();
            }
        });
    },
    // 千分位
    thousands: function (number) {
        // let reg = /\d{1,3}(?=(\d{3})+$)/g;
        let reg = /(\d{1,3})(?=(\d{3})+(?:$|\.))/g;
        return (number + "").replace(reg, "$1,");
    },
    // 数值进位 kb,mb,gb
    evolveNumber: function (number, symbol) {
        let result = 0;
        if (!msisAPP.objIsNumber(number)) {
            return number + "";
        }
        if (/kb/ig.test(symbol)) {
            result = Math.round(number / 1024 * 100) / 100;
        } else if (/mb/ig.test(symbol)) {
            result = Math.round(number / 1024 / 1024 * 100) / 100;
        } else if (/gb/ig.test(symbol)) {
            result = Math.round(number / 1024 / 1024 / 1024 * 100) / 100;
        } else if (/pb/ig.test(symbol)) {
            result = Math.round(number / 1024 / 1024 / 1024 / 1024 * 100) / 100;
        } else {
            result = number;
        }
        return result + "";


    },
    // 判断任何一个对象是否能完全转化为int类型
    objIsNumber: function (obj) {
        let s = String(obj);
        let i = String(parseInt(s));
        return i !== "NaN" ? s.length === i.length : false;
    },


// 站点群下拉列表
    groupsList: function () {
        let options = [];
        let $groupsList = $("#groupsList option");
        let ajaxUrl = "";
        let groupListType = "";
        let groupID = GetQueryString('groupID');
        let siteID = GetQueryString('siteID');
        let unitID = GetQueryString('unitID');
        let request_url = document.referrer;
        let url = document.location.href;

        if (request_url.indexOf("manage") > -1 && url.indexOf("back") > -1 && siteID !== "-1" && unitID !== "-1") {
            groupListType = "admin";
            ajaxUrl = "/integrator/homePage/groupsListManage?groupID=" + groupID + "&siteId=-2&unitId=-2";
        } else if (siteID === "-1" && unitID === "-1") {
            groupListType = "institute";
            ajaxUrl = "/integrator/homePage/groupsListManage?groupID=" + groupID + "&siteId=-1&unitId=-1"
        } else {
            groupListType = "normal";
            options = options.concat(["<option value='1'>科学数据库</option>"]);
            ajaxUrl = "/integrator/homePage/groupsList";
        }

        function formatSelect(result) {
            let isShowArray = ["<optgroup label=\"公开站点群\">"];
            let noShowArray = ["<optgroup label=非公开站点群>"];
            if (groupListType === "normal") {
                $.each(result, function (index, value) {
                    options = options.concat(["<option value='" + value.id + "'>&nbsp;&nbsp;--" + value.name + "</option>"]);
                });
            } else if (groupListType === "admin") {
                $.each(result, function (index, value) {
                    if (value.gtype === "20") {
                        options = ["<option value='" + value.id + "'>" + value.name + "</option>"].concat(options);
                    } else {
                        options = options.concat(["<option value='" + value.id + "'>&nbsp;&nbsp;--" + value.name + "</option>"]);
                    }
                })
            } else {
                $.each(result, function (index, value) {
                    if (value.isShow === "0") {
                        noShowArray = noShowArray.concat(["<option value='" + value.id + "'>&nbsp;&nbsp;--" + value.name + "</option>"]);
                    } else if (value.isShow === "1") {
                        isShowArray = isShowArray.concat(["<option value='" + value.id + "'>&nbsp;&nbsp;--" + value.name + "</option>"]);
                    } else {
                        options = options.concat(["<option value='" + value.id + "'>" + value.name + "</option>"]);
                    }
                });
                options = options.concat(isShowArray).concat(["</optgroup>"]);
                options = options.concat(noShowArray).concat(["</optgroup>"]);
            }
            $groupsList.remove();
            options = ["<option value='0'>选择数据库</option>"].concat(options);
            $("#groupsList").append(options.join(""));
        }

        $.ajax({
            url: ajaxUrl,
            type: "POST",
            async: false,
            dataType: "JSON",
            success: function (result) {
                formatSelect(result);
            }
        });
        if (request_url.indexOf("manage") > -1 && url.indexOf("back") > -1 && siteID !== "-1" && unitID !== "-1") {
            $("#groupsList").val(unitID);
        } else if (siteID === "-1" && unitID === "-1") {
            $("#groupsList").val(groupID);
        }
    },
    layerOpen: function () {
        //一个div，用来遮挡页面，请求过程中，不可操作页面
        let lockwin = $("#layer");
        //div占满整个页面
        lockwin.css("width", "100%");
        lockwin.css("display", "block");
        lockwin.css("height", "100%");
        //设置图片居中
        $("#layer img").css("display", "block");
        $("#layer img").css("left", ($(window).width() - 88) / 2);
        $("#layer img").css("top", ($(window).height() + $(window).scrollTop()) / 2);
    },
    layerClose: function () {
        //隐藏div
        let lockwin = $("#layer");
        lockwin.css("width", "0");
        lockwin.css("display", "none");
        lockwin.css("height", "0");
        //设置图片隐藏
        $("#layer img").css("display", "none");
    },
    // radio时间段转换
    radioTimeConvert: function (symbol) {
        let result = {};
        let year = new Date().getFullYear();
        let type = symbol.split("-")[0];
        let value = symbol.split("-")[1];
        if (type === "quarter") {
            switch (value) {
                case "1":
                    result["begin"] = "" + year + "0101";
                    result["end"] = "" + year + "0331";
                    break;
                case "2":
                    result["begin"] = "" + year + "0401";
                    result["end"] = "" + year + "0630";
                    break;
                case "3":
                    result["begin"] = "" + year + "0701";
                    result["end"] = "" + year + "0931";
                    break;
                case "4":
                    result["begin"] = "" + year + "1001";
                    result["end"] = "" + year + "1231";
                    break;
            }
        } else if (type === "halfYear") {
            switch (value) {
                case "1":
                    result["begin"] = "" + year + "0101";
                    result["end"] = "" + year + "0630";
                    break;
                case "2":
                    result["begin"] = "" + year + "0701";
                    result["end"] = "" + year + "1231";
                    break;
            }
        } else if (type === "year") {
            result["begin"] = "" + year + "0101";
            result["end"] = "" + year + "1231";
        }
        return result;
    },
    // 时间插件转换
    selectTimeConvert: function (time) {
        let result = {};
        let s = time.split("\/");
        let year;
        let month;
        if (s.length === 3) {
            // 末位是日
            year = s[0];
            month = s[1];
            let day = s[2];
            result["begin"] = "" + year + month + day;
        } else if (s.length === 2) {
            // 末位是月
            year = s[0];
            month = s[1];
            result["begin"] = "" + year + month + "01";
            let arr1 = [1, 3, 5, 7, 8, 10, 12];
            let arr2 = [4, 6, 9, 11];
            if (arr1.indexOf(parseInt(month)) > -1) {
                result["end"] = "" + year + month + "31";
            } else if (arr2.indexOf(parseInt(month)) > -1) {
                result["end"] = "" + year + month + "30";
            } else {
                // 闰年判断
                if ((year % 4 === 0 && year % 100 !== 0) || (year % 100 === 0 && year % 400 === 0)) {
                    result["end"] = "" + year + month + "29";
                } else {
                    result["end"] = "" + year + month + "28";
                }
            }
        } else if (s.length === 1) {
            year = s[0];
            result["begin"] = "" + year + "0101";
            result["end"] = "" + year + "1231";
        } else {
            return result;
        }

        return result;
    },
    // 判断JSON是否为空或null
    jsonIsEmpty: function (json) {
        let s = JSON.stringify(json);
        if (s === "{}" || s === "null") {
            return true;
        }
        return false;
    },
    // 获取日志中最早的时间
    datepickerStartMonth: function () {
        let datepickerStartMonth = new Date();
        $.ajax({
            url: "/startTime",
            type: "POST",
            async: false,
            success: function (result) {
                let s = result.split("\/");
                datepickerStartMonth.setFullYear(s[0], s[1] - 1, s[1]);
            }
        });
        return datepickerStartMonth;
    }
};