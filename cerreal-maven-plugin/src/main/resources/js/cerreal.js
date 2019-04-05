cerreal_toggleDisplay = function(elementId) {
    var el = document.getElementById(elementId);
    if (el.style.display != 'none') {
        el.style.display = 'none';
    } else {
        el.style.display = '';        
    }
}
cerreal_getStyle = function(eType) {
    var styleE = document.getElementById(eType + 'Style');
    if (!styleE) {
        styleE = document.createElement('style');
        styleE.id = eType + 'Style';
        document.head.appendChild(styleE);
    }
    return styleE;
}
cerreal_toggleClassDisplay = function(classtype, classname) {
    var styleE = cerreal_getStyle(classname);
    try {
        styleE.sheet.deleteRule(0);
    } catch (e) {
        styleE.sheet.insertRule(classtype + '.' + classname + '{display: none;}', 0);
    }

    var toggleElements = document.getElementsByClassName(classname + 'Toggle');
    var idx = 0;
    var el = null;
    for (idx = 0; idx < toggleElements.length; idx++) {
        el = toggleElements[idx];
        if (el.classList.contains("a")) {
            el.classList.remove("a");
            el.classList.add("b");
        } else if (el.classList.contains("b")) {
            el.classList.remove("b");
            el.classList.add("a");
        }
    }
}
