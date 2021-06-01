Ajax.Responders.register({
  onCreate: function(request) {
	  Utils.startWaiting();
	  if (Object.isUndefined(request.options.content)) {
		  return;
	  }
      var containers = new Array();
      if (Object.isArray(request.options.content)) {
          containers = $A(request.options.content);
      } else {
          containers.push(request.options.content)
          containers = $A(containers)
      }
      containers.each(function(container) {
          $(container).startWaiting();
      });
  },
  onComplete: function(request) {
	  if(Ajax.activeRequestCount < 1) {
		  Utils.stopWaiting();
	  }
	  if (Object.isUndefined(request.options.content)) {
		  return;
	  }
      var containers = new Array();
      if (Object.isArray(request.options.content)) {
          containers = $A(request.options.content);
      } else {
          containers.push(request.options.content)
          containers = $A(containers)
      }
      containers.each(function(container) {
          $(container).stopWaiting();
      });
  },
  onException: function(request) {
	  // exception if Ajax.Responder throw exception 
	  Utils.stopWaiting();
	  if (Object.isUndefined(request.options.content)) {
		  return;
	  }
      var containers = new Array();
      if (Object.isArray(request.options.content)) {
          containers = $A(request.options.content);
      } else {
          containers.push(request.options.content)
          containers = $A(containers)
      }
      containers.each(function(container) {
          $(container).stopWaiting();
      });
  }
});

