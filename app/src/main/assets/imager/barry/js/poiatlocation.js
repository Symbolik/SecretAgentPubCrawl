
// implementation of AR-Experience (aka "World")
var World = {
	// true once data was fetched
	initiallyLoadedData: false,

	// POI-Marker asset
	markerDrawable_idle: null,
	
	markerFilename : null,
	
	markerImageDrawable_idle : null,
	

	isWithinFrame: false,

	markerObject : null,
	
	markerLocation :null,

	init: function initFn(){
	
	},
	
	// called to inject new POI data, happens every second when location change is updated
	loadPoisFromJsonData: function loadPoisFromJsonDataFn(poiData) {
		World.markerDrawable_idle = new AR.ImageResource(markerFilename);
		
		// create the marker
		World.markerLocation = new AR.GeoLocation(poiData.latitude, poiData.longitude, poiData.altitude);
		
		World.markerImageDrawable_idle = new AR.ImageDrawable(World.markerDrawable_idle, 2.5, {
			zOrder: 0,
			opacity: 1,
			scale: 5,
		});

		

		// create GeoObject
		World.markerObject = new AR.GeoObject(World.markerLocation, {
			drawables: {
				cam: [World.markerImageDrawable_idle]
			},
			onEnterFieldOfVision: this.appear,
			onExitFieldOfVision : this.disappear
		});
	
	},

	// updates status message shon in small "i"-button aligned bottom center
	updateStatusMessage: function updateStatusMessageFn(message, isWarning) {

		var themeToUse = isWarning ? "e" : "c";
		var iconToUse = isWarning ? "alert" : "info";

		$("#status-message").html(message);
		$("#popupInfoButton").buttonMarkup({
			theme: themeToUse
		});
		$("#popupInfoButton").buttonMarkup({
			icon: iconToUse
		});
	},

	
	setGhostMarker: function setGhostMarkerFn(ghostNum) {
	    markerFilename = "assets/ramlogo.png";
	    /*
		switch(ghostNum){
			case 1:
				markerFilename = "assets/Ghost1.png";
			break;
			case 2:
				markerFilename = "assets/Ghost2.png";
			break;
			case 3:
				markerFilename = "assets/Ghost3.png";
			break;
		}*/
	},


	locationChanged: function locationChangedFn(lat, lon, alt, acc) {

		// request data if not already present
		if (!World.initiallyLoadedData) {
			var poiData = {
				"id": 1,
				"longitude": (lon + (Math.random() / 5 - 0.1)),
				"latitude": (lat + (Math.random() / 5 - 0.1)),
				"altitude": 100.0
			};
			
			AR.logger.debug("poidata, lonlat " + poiData.longitude.toString());
			

			//use same POI for now, create shadowy effect
			World.loadPoisFromJsonData(poiData);
			World.initiallyLoadedData = true;
		}
	},
	
	// reload places from content source
	captureScreen: function captureScreenFn() {
		document.location = "architectsdk://button?visible=" + World.isWithinFrame;
	},
	
	// screen was clicked but no geo-object was hit
	onScreenClick: function onScreenClickFn() {
		// you may handle clicks on empty AR space too
	},

	disappear: function disappearFn() {
	    World.isWithinFrame = false;
	    document.location = "architectsdk://exit";
    },

	appear: function appearFn() {
	    document.location = "architectsdk://enter";
	    World.isWithinFrame = true;

	}
	
	
};

/* forward clicks in empty area to World */
AR.context.onScreenClick = World.onScreenClick;



/* forward locationChanges to custom function */
AR.context.onLocationChanged = World.locationChanged;
setTimeout(function(){World.init()}, 10000);