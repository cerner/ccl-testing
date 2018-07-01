cerreal_toggleDisplay = function(elementId) {
    var el = document.getElementById(elementId);
    if (el.style.display != 'none') {
        el.style.display = 'none';
    } else {
        el.style.display = '';        
    }
}
