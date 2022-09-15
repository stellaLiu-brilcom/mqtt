<%@page import="java.time.LocalTime"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.TimeZone"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
<meta charset="UTF-8">
<META HTTP-EQUIV="refresh" CONTENT="1800"> <!-- 5분 -->
<title>My DashBoard</title>

<script type="text/javascript" src="http://code.jquery.com/jquery-3.5.1.min.js"></script>
<script src="https://code.highcharts.com/highcharts.js"></script>
<script src="https://code.highcharts.com/highcharts-more.js"></script>
<script src="https://code.highcharts.com/modules/solid-gauge.js"></script>
<script src="https://code.highcharts.com/modules/series-label.js"></script>
<script src="https://code.highcharts.com/modules/exporting.js"></script>
<script src="https://code.highcharts.com/modules/export-data.js"></script>
<script src="https://code.highcharts.com/modules/accessibility.js"></script>

<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Noto+Sans:wght@300&display=swap" rel="stylesheet">

<link rel="shortcut icon" type="image/x-icon" href="1024_1024_logo.png">


<style type="text/css">

body{
font-family: 'Noto Sans', sans-serif;
}

.img{
	align : left;
}

#divClock{
    float:right; 

}

#coName{
    position: static; 
    left: 0px; 
    bottom: 0px; 
	font-size: 16px;
    color: '#BFBFBF'; /* 191, 191, 191 */
}  


.responsive_mobile{
    display:none;
}



@media screen and (max-device-width: 500px) {
    .responsive_mobile{
        display:block !important;
    }

    .responsive_window{ 
        display:none !important;
    }

    }









</style>

<script type="text/javascript">
      function showClock(){
        var currentDate = new Date();
        var divClock = document.getElementById('divClock');
        
        var msg="";// = "현재 시간 : ";
     
        msg += currentDate.toLocaleString();
        
 
        divClock.innerText = msg;
 

        setTimeout(showClock,1000);  //1초마다 갱신
      }
</script>


<%

String insertNum = request.getParameter("serialNum");
System.out.println(insertNum);

SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

Calendar cal = Calendar.getInstance();
cal.add(Calendar.HOUR, -13);
String startDate = sdf.format(cal.getTime());
System.out.println(startDate);
cal.add(Calendar.HOUR, +13);
String endDate = sdf.format(cal.getTime());
System.out.println(endDate);

%>


<script type="text/javascript">
var _data;
var response;
var res;


$(document).ready(function() {

    var settings = {
            "url" : "/mqtt/GetAirQualityChart",
            "method" : "POST",
            "timeout" : 0,
            "headers" : {"Content-Type" : "application/json"},
            "data" : JSON.stringify({
                "serialNum" : "<%=insertNum%>",
                "startTime" : "<%=startDate%>",
                "endTime" : "<%=endDate%>",
                "type" : "Co2,Humid,Pm10,Pm25,Temperature,Tvoc"
                }),
    }; // end of setting

    $.ajax(settings).done(function(res){
        this.response = res;
        console.log(res);
        console.log("_________________________________________________");
      
      //Temp 원형
      function renderIcons() {

}
 

      $(function () {

            Highcharts.chart('container', {
                chart: {
                    type: 'solidgauge',
                    height : '110%',
                    //marginTop: 50
                },
                title: {
                      text: '<b>온도</b><br/>(Temperature)',
                      subtext :'',
                      style: {
                          fontSize: '30px'
                      }
                  },
                  credits :{
                      enabled : false
                  },
                tooltip: {
                    borderWidth: 0,
                    backgroundColor: 'none',
                    shadow: false,
                    style: {
                        fontSize: '30px'
                    },
                    pointFormat: '{series.name}<br><span style="font-size:30px; color: #000000; font-weight: bold">{point.y}</span>'+ 
									'<span style="font-size:16px; color:#000000; font-weight: bold">°C</span>',
                    positioner: function (labelWidth, labelHeight) {
                        return {
                            x : 265,
                            y : 345,
                        };
                    }
                },
                pane: {
                    size : '100%',
                    startAngle: 0,
                    endAngle: 360,
                    background: [{ // Track for Exercise
                        outerRadius: '62%',
                        innerRadius: '38%',
                        //backgroundColor: Highcharts.Color(Highcharts.getOptions().colors[2]).setOpacity(0.3).get(),
                        borderWidth: 0
                    }]
                },
                exporting: {
                      enabled: false
                  },
                yAxis: {
                    min: 0,
                    max: 100,
                    lineWidth: 0,
                    tickPositions: []
                },
                plotOptions: {
                    solidgauge: {
                        dataLabels: {
                            enabled: false
                        },
                        linecap: 'round',
                        stickyTracking: false,
                        rounded: true
                    }
                },
                series: [{
                    name: '',
                    //borderColor: Highcharts.getOptions().colors[2],
                    data: [{
                        color: '#5551FF',//Highcharts.getOptions().colors[2],
                        radius: '62%',
                        innerRadius: '38%',
                        y: res.data[660].Temperature*10/10,//res.data[600].Temperature.split('.')[0],
                    }],
                    dataLabels: {
                           formatter: function () {
                              var temp = this.y;
                              return '<span>' + temp + ' °C</span><br/><br/>' ;
                          }, 
                      },
                      pane: {
                            background: {
                              borderWidth: 0
                            }
                          },
                }]
            },

            function callback() {
                
            });
            var chart = $('#container').highcharts(),
                  point = chart.series[0].points[0];
            point.onMouseOver(); // Show the hover marker
            chart.tooltip.refresh(point); // Show the tooltip
            chart.tooltip.hide = function () {console.log()};
        });
      
      
    //humid 원형 모바일 전용뷰 : 습도
      Highcharts.chart('container2', {
        chart: {
            type: 'solidgauge',
            height : '110%',
            //marginTop: 50
        },
        title: {
              text: '<b>습도</b><br/>(Humidity)',
              subtext :'',
              style: {
                  fontSize: '30px'
              }
          },
          credits :{
              enabled : false
          },
        tooltip: {
            borderWidth: 0,
            backgroundColor: 'none',
            shadow: false,
            style: {
                fontSize: '30px'
            },
            pointFormat: '{series.name}<br><span style="font-size:30px; color: #000000; font-weight: bold">{point.y}</span>'+ '<span style="font-size:16px; color:#000000; font-weight: bold;">%</span>',
            positioner: function (labelWidth, labelHeight) {
                return {
                    x : 265,
					y : 345,
                };
            }
        },
        pane: {
            size : '100%',
            startAngle: 0,
            endAngle: 360,
            background: [{ // Track for Exercise
                outerRadius: '62%',//'87%',
                innerRadius: '38%',//'63%',
                borderWidth: 0
            }]
        },
        exporting: {
              enabled: false
          },
        yAxis: {
            min: 0,
            max: 100,
            lineWidth: 0,
            tickPositions: []
        },
        plotOptions: {
            solidgauge: {
                //borderWidth: '34px',
                dataLabels: {
                    enabled: false
                },
                linecap: 'round',
                stickyTracking: false,
                rounded: true
            }
        },
        series: [{
            name: '',
            //borderColor: Highcharts.getOptions().colors[2],
            data: [{
                color: '#5551FF',//Highcharts.getOptions().colors[2],
                radius: '62%',
                innerRadius: '38%',
                y: res.data[660].Humid*1000/1000
            }],
            dataLabels: {
                   formatter: function () {
                      var humid = this.y;
                      return '<span>' + humid + ' %</span><br/><br/>' ;
                  }, 
              },
              /*tooltip: {
                  valueSuffix: '%',
				  style: {
                        fontSize: '30px'
                    },
              },*/
              pane: {
                    background: {
                      borderWidth: 0
                    }
                  },
        }]
    },

    function callback() {

    });
    var chart = $('#container2').highcharts(),
          point = chart.series[0].points[0];
    point.onMouseOver(); // Show the hover marker
    chart.tooltip.refresh(point); // Show the tooltip
    chart.tooltip.hide = function () {console.log()};
 



    //온도 모바일뷰 전용
    
            Highcharts.chart('container3', {
                chart: {
                    type: 'solidgauge',
                    height : '110%',
                    //marginTop: 50
                },
                title: {
                      text: '<b>온도</b><br/>(Temperature)',
                      style: {
                          fontSize: '30px'
                      }
                  },
                  credits :{
                      enabled : false
                  },
                tooltip: {
                    borderWidth: 0,
                    backgroundColor: 'none',
                    shadow: false,
                    style: {
                        fontSize: '30px'
                    },
                    pointFormat: '{series.name}<br><span style="font-size:30px; color: #000000; font-weight: bold">{point.y}</span>'+ '<span style="font-size:16px; color:#000000; font-weight: bold;">°C</span>',
                    positioner: function (labelWidth, labelHeight) {
                        return {
                            x : 265,
                            y : 345,
                        };
                    }
                },
                //tooltip: { enabled: true },
                pane: {
                    size : '100%',
                    startAngle: 0,
                    endAngle: 360,
                    background: [{ // Track for Exercise
                        outerRadius: '62%',//'87%',
                        innerRadius: '38%',//'63%',
                        //backgroundColor: Highcharts.Color(Highcharts.getOptions().colors[2]).setOpacity(0.3).get(),
                        borderWidth: 0
                    }]
                },
                exporting: {
                      enabled: false
                  },
                yAxis: {
                    min: 0,
                    max: 100,
                    lineWidth: 0,
                    tickPositions: []
                },
                plotOptions: {
                    solidgauge: {
                        //borderWidth: '34px',
                        dataLabels: {
                            enabled: false
                        },
                        linecap: 'round',
                        stickyTracking: false,
                        rounded: true
                    }
                },
                series: [{
                    name: '',
                    borderColor: Highcharts.getOptions().colors[2],
                    data: [{
                        color: '#5551FF',//Highcharts.getOptions().colors[2],
                        radius: '62%',
                        innerRadius: '38%',
                        y: res.data[660].Temperature*10/10,//res.data[600].Temperature.split('.')[0],
                    }],
                    dataLabels: {
                           formatter: function () {
                              var temp = this.y;
                              return '<span>' + temp + ' °C</span><br/><br/>' ;
                          }, 
                      },
                      /*tooltip: {
                          valueSuffix: '°C',
						  
                      },*/
                      pane: {
                            background: {
                              borderWidth: 0
                            }
                          },
                }]
            },

            function callback() {
            });
            var chart = $('#container3').highcharts(),
                  point = chart.series[0].points[0];
            point.onMouseOver(); // Show the hover marker
            chart.tooltip.refresh(point); // Show the tooltip
            chart.tooltip.hide = function () {console.log()};



     //humid 원형
    Highcharts.chart('container4', {
        chart: {
            type: 'solidgauge',
            height : '110%',
        },
        title: {
              text: '<b>습도</b><br/>(Humidity)',
              subtext :'',
              style: {
                  fontSize: '30px'
              }
          },
          credits :{
              enabled : false
          },
        tooltip: {
            borderWidth: 0,
            backgroundColor: 'none',
            shadow: false,
            style: {
                fontSize: '30px'
            },
            pointFormat: '{series.name}<br><span style="font-size:30px; color: #000000; font-weight: bold">{point.y}</span>'+ '<span style="font-size:16px; color:#000000; font-weight: bold;">%</span>',
            positioner: function (labelWidth, labelHeight) {
                return {
                    x : 265,
					y : 345,
                };
            }
        },
        //tooltip: { enabled: true },
        pane: {
            size : '100%',
            startAngle: 0,
            endAngle: 360,
            background: [{ // Track for Exercise
                outerRadius: '62%',//'87%',
                innerRadius: '38%',//'63%',
                //backgroundColor: Highcharts.Color(Highcharts.getOptions().colors[4]).setOpacity(0.3).get(),
                borderWidth: 0
            }]
        },
        exporting: {
              enabled: false
          },
        yAxis: {
            min: 0,
            max: 100,
            lineWidth: 0,
            tickPositions: []
        },
        plotOptions: {
            solidgauge: {
                //borderWidth: '34px',
                dataLabels: {
                    enabled: false
                },
                linecap: 'round',
                stickyTracking: false,
                rounded: true
            }
        },
        series: [{
            name: '',
            //borderColor: Highcharts.getOptions().colors[2],
            data: [{
                color: '#5551FF',//Highcharts.getOptions().colors[2],
                radius: '62%',
                innerRadius: '38%',
                y: res.data[660].Humid*1000/1000
            }],
            dataLabels: {
                   formatter: function () {
                      var humid = this.y;
                      return '<span>' + humid + ' %</span><br/><br/>' ;
                  }, 
              },
              /*tooltip: {
                  valueSuffix: '%',
				  style: {
                              fontsize: '16px'
                          },
              }, */
              pane: {
                    background: {
                      borderWidth: 0
                    }
                  },
        }]
    },

    function callback() {

    });
    var chart = $('#container4').highcharts(),
          point = chart.series[0].points[0];
    point.onMouseOver(); // Show the hover marker
    chart.tooltip.refresh(point); // Show the tooltip
    chart.tooltip.hide = function () {console.log()};
   
    
    

    
      //pm25 반원 모바일 전용뷰
               var pm25Options_m = {
                       chart: {
                           type: 'solidgauge',
                       },
               
                       title: null,
               
                       pane: {
                           center: ['50%', '85%'],
                           size: '80%',
                           startAngle: -90,
                           endAngle: 90,
                           background: {
                               backgroundColor:
                                   Highcharts.defaultOptions.legend.backgroundColor || '#EEE',
                               innerRadius: '60%',
                               outerRadius: '100%',
                               shape: 'arc'
                           }
                       },
                       exporting: {
                           enabled: false
                       },
               
                       tooltip: {
                           enabled: false
                       },
               
                       // the value axis
                       yAxis: {
						   stops: [
                               [0.15, '#79D2FF'], // skyblue
                               [0.35, '#79BE00'], // green
                               [0.75, '#FEA040'], // yellow
                               [1, '#FD5344'], // red
                           ],
						   lineWidth: 0,
                           tickWidth: 0,
                           minorTickInterval: null,
                           tickWidth: 0,
                           endOnTick: false,
                           maxPadding: 0,
                           title: {
                               y: -110,
                           },
                           labels: {
                               y: 26.5
                           }
                       },
               
                       plotOptions: {
                           solidgauge: {
                               dataLabels: {
                                   y: 5,
                                   borderWidth: 0,
                                   useHTML: true,
                               },
                           }
                       }
                   }; //end of pm25Options


               // The Pm25 gauge
               var chartPm25_m = Highcharts.chart('pm25_m', Highcharts.merge(pm25Options_m, {
                   yAxis: {
                       min: 1,
                       max: 100,
					   tickPositioner: function() {
                           return [this.min, this.max];
                         },
                       title: {
                           text: '초미세먼지(PM2.5)',
                           style: {
                               color: '#7F7F7F',
                               fontSize: '30px',
                               }
                       }
                   },

                   credits: {
                       enabled: false //하이차트 로고
                   },

                   series: [{
                       name: '초미세먼지(PM2.5)',
                       data: [res.data[660].Pm25*1000/1000],
                       dataLabels: {
						   y : -40,
                           format:
                               '<div style="text-align:center">' +
                               '<span style="font-size:25px; color: #7F7F7F;">{y}</span>' +
                               '<span style="font-size:16px;color: #7F7F7F;">㎍/㎥</span>' +
                               '</div>'
                       },
                       tooltip: {
                           valueSuffix: '㎍/㎥'
                       }
                   }] //end of series

               })); //chartPm25


    
      //pm25 브라우저 반원
               var pm25Options = {
                       chart: {
                           type: 'solidgauge',
                       },
               
                       title: null,
               
                       pane: {
                           center: ['50%', '85%'],
                           size: '80%',
                           startAngle: -90,
                           endAngle: 90,
                           background: {
                               backgroundColor:
                                   Highcharts.defaultOptions.legend.backgroundColor || '#EEE',
                               innerRadius: '60%',
                               outerRadius: '100%',
                               shape: 'arc'
                           }
                       },
                       exporting: {
                           enabled: false
                       },
               
                       tooltip: {
                           enabled: false
                       },
               
                       // the value axis
                       yAxis: {
						   stops: [
                               [0.15, '#79D2FF'], // skyblue
                               [0.35, '#79BE00'], // green
                               [0.75, '#FEA040'], // yellow
                               [1, '#FD5344'], // red
                           ],
                           lineWidth: 0,
                           tickWidth: 0,
                           //minorTickInterval:10,
                           minorTickInterval: null,
                           //tickAmount: 2,
                           tickWidth: 0,
                           endOnTick: false,
                           maxPadding: 0,
                           title: {
                               y: -110,
                           },
                           labels: {
                               y: 26.5
                           }
                       },
               
                       plotOptions: {
                           solidgauge: {
                               dataLabels: {
                                   y: 20,
                                   borderWidth: 0,
                                   useHTML: true,
                               },
                               //rounded: true
                           }
                       }
                   }; //end of pm25Options

               // The Pm25 gauge
               var chartPm25 = Highcharts.chart('pm25', Highcharts.merge(pm25Options, {
                   yAxis: {
                       min: 1,
                       max: 100,
                       tickPositioner: function() {
                           return [this.min, this.max];
                         },
                       title: {
                           text: '초미세먼지(PM2.5)',
                           style: {
                               color: '#7F7F7F',
                               fontSize: '30px',
                               }
                       }
                   },

                   credits: {
                       enabled: false //하이차트 로고
                   },

                   series: [{
                       name: '초미세먼지(PM2.5)',
                       data: [res.data[660].Pm25*1000/1000],
                       dataLabels: {
						   y : -40,
                           format:
                               '<div style="text-align:center">' +
                               '<span style="font-size:25px; color: #7F7F7F;">{y}</span> &nbsp' +
                               '<span style="font-size:16px;color: #7F7F7F;">㎍/㎥</span>' +
                               '</div>'
                       },
                       tooltip: {
                           valueSuffix: '㎍/㎥'
                       }
                   }] //end of series

               })); //chartPm25


               
               //pm10 chart
                  var pm10Options = {
                       chart: {
                           type: 'solidgauge'
                       },
               
                       title: null,
               
                       pane: {
                           center: ['50%', '85%'],
                           size: '80%',
                           startAngle: -90,
                           endAngle: 90,
                           background: {
                               backgroundColor:
                                   Highcharts.defaultOptions.legend.backgroundColor || '#EEE',
                               innerRadius: '60%',
                               outerRadius: '100%',
                               shape: 'arc'
                           }
                       },
               
                       exporting: {
                           enabled: false
                       },
               
                       tooltip: {
                           enabled: false
                       },
               
                       // the value axis
                       yAxis: {
                           stops: [
                               [0.15, '#79D2FF'], // skyblue
                               [0.4, '#79BE00'], // green
                               [0.75, '#FEA040'], // yellow
                               [1, '#FD5344'], // red
                           ],
                           lineWidth: 0,
                           tickWidth: 0,
                           minorTickInterval: null,
                           tickWidth: 0,
                           endOnTick: false,
                           maxPadding: 0,
                           title: {
                               y: -110
                           },
                           labels: {
                               y: 26.5
                           }
                       },
               
                       plotOptions: {
                           solidgauge: {
                               dataLabels: {
                                   y: 10,
                                   borderWidth: 0,
                                   useHTML: true
                               }
                           }
                       }
                   }; //end of pm10Options

               // The Pm10 gauge
               var chartPm10 = Highcharts.chart('pm10', Highcharts.merge(pm10Options, {
                   yAxis: {
                       min: 1,
                       max: 200,
					   tickPositioner: function() {
                           return [this.min, this.max];
                         },
                       title: {
                           text: '미세먼지(PM10)',
                           style: {
                               color: '#7F7F7F',
                               fontSize: '30px',
                               }
                       }
                   },

                   credits: {
                       enabled: false
                   },

                   series: [{
                       name: '미세먼지(Pm10)',
                       data: [ res.data[660].Pm10*1000/1000],
					   dataLabels: {
                    	   y : -40,
                           format:
                               '<div style="text-align:center">' +
                               '<span style="font-size:25px; color: #7F7F7F;">{y}</span> &nbsp' +
                               '<span style="font-size:16px;color: #7F7F7F;">㎍/㎥</span>' +
                               '</div>'
                       },
                       tooltip: {
                           valueSuffix: '㎍/㎥'
                       }
                   }] //end of series

               })); //chartPm10
   

                  //pm10 chart
                  var pm10Options_m = {
                    chart: {
                        type: 'solidgauge'
                    },
            
                    title: null,
            
                    pane: {
                        center: ['50%', '85%'],
                        size: '80%',
                        startAngle: -90,
                        endAngle: 90,
                        background: {
                            backgroundColor:
                                Highcharts.defaultOptions.legend.backgroundColor || '#EEE',
                            innerRadius: '60%',
                            outerRadius: '100%',
                            shape: 'arc'
                        }
                    },
            
                    exporting: {
                        enabled: false
                    },
            
                    tooltip: {
                        enabled: false
                    },
            
                    // the value axis
                    yAxis: {
                        stops: [
                               [0.15, '#79D2FF'], // skyblue
                               [0.4, '#79BE00'], // green
                               [0.75, '#FEA040'], // yellow
                               [1, '#FD5344'], // red
                           ],
                        lineWidth: 0,
                           tickWidth: 0,
                           minorTickInterval: null,
                           tickWidth: 0,
                           endOnTick: false,
                           maxPadding: 0,
                        title: {
                            y: -110
                        },
                        labels: {
                            y: 26.5
                        }
                    },
                    plotOptions: {
                        solidgauge: {
                            dataLabels: {
                                y: 10,
                                borderWidth: 0,
                                useHTML: true
                            }
                        }
                    }
                }; //end of pm10Options

            // The Pm10 gauge
            var chartPm10_m = Highcharts.chart('pm10_m', Highcharts.merge(pm10Options_m, {
                yAxis: {
                    min: 1,
                    max: 200,
					tickPositioner: function() {
                           return [this.min, this.max];
                         },
                    title: {
                        text: '미세먼지(PM10)',
                        style: {
                            color: '#7F7F7F',
                            fontSize: '30px',
                            }
                    }
                },

                credits: {
                    enabled: false
                },

                series: [{
                    name: '미세먼지(PM10)',
                    data: [ res.data[660].Pm10*1000/1000],
					dataLabels: {
                    	   y : -40,
                           format:
                               '<div style="text-align:center">' +
                               '<span style="font-size:25px; color: #7F7F7F;">{y}</span> &nbsp' +
                               '<span style="font-size:16px;color: #7F7F7F;">㎍/㎥</span>' +
                               '</div>'
                       },
                    tooltip: {
                        valueSuffix: '㎍/㎥'
                    }
                }] //end of series

            })); //chartPm10
               
               //tvoc
                   var tvocOptions = {
                       chart: {
                           type: 'solidgauge'
                       },
               
                       title: null,
               
                       pane: {
                           center: ['50%', '85%'],
                           size: '80%',
                           startAngle: -90,
                           endAngle: 90,
                           background: {
                               backgroundColor:
                                   Highcharts.defaultOptions.legend.backgroundColor || '#EEE',
                               innerRadius: '60%',
                               outerRadius: '100%',
                               shape: 'arc'
                           }
                       },
               
                       exporting: {
                           enabled: false
                       },
               
                       tooltip: {
                           enabled: false
                       },
               
                       // the value axis
                       yAxis: {
                           stops: [
                               [0.249, '#79D2FF'], // skyblue
                               [0.449, '#79BE00'], // green
                               [1, '#FD5344'], // red
                           ], 
                           lineWidth: 0,
                           tickWidth: 0,
                           minorTickInterval: null,
                           tickWidth: 0,
                           endOnTick: false,
                           maxPadding: 0,
                           title: {
                               y: -110
                           },
                           labels: {
                               y: 26.5
                           }
                       },
               
                       plotOptions: {
                           solidgauge: {
                               dataLabels: {
                                   y: 5,
                                   borderWidth: 0,
                                   useHTML: true
                               }
                           }
                       }
                   }; //end of tvocOptions

               // The Tvoc gauge
               var chartTvoc = Highcharts.chart('tvoc', Highcharts.merge(tvocOptions, {
                   yAxis: {
                       min: 1,
                       max: 1000,
					   tickPositioner: function() {
                           return [this.min, this.max];
                         },
                       title: {
                           text: '휘발성유기화합물(TVOC)',
                           style: {
                               color: '#7F7F7F',
                               fontSize: '30px',
                               }
                       }
                   },

                   credits: {
                       enabled: false
                   },

                   series: [{
                       name: '휘발성유기화합물(Tvoc)',
                       data: [ res.data[660].Tvoc*1000/1000],
					   dataLabels: {
                    	   y : -40,
                           format:
                               '<div style="text-align:center">' +
                               '<span style="font-size:25px; color: #7F7F7F;">{y}</span> &nbsp' +
                               '<span style="font-size:16px;color: #7F7F7F;">ppb</span>' +
                               '</div>'
                       },
                       tooltip: {
                           valueSuffix: 'ppb'
                       }
                   }] //end of series

               })); //chartTvoc
               



                // The Tvoc gauge 
                //tvoc 모바일전용뷰
                var chartTvoc = Highcharts.chart('tvoc_m', Highcharts.merge(tvocOptions, {
                    yAxis: {
                        min: 1,
                        max: 1000,
						tickPositioner: function() {
                           return [this.min, this.max];
                         },
                        title: {
                            text: '휘발성유기화합물(Tvoc)',
                            style: {
                                color: '#7F7F7F',
                                fontSize: '30px',
                                }
                        }
                    },
 
                    credits: {
                        enabled: false
                    },
 
                    series: [{
                        name: '휘발성유기화합물(TVOC)',
                        data: [ res.data[660].Tvoc*1000/1000],
                        dataLabels: {
							y : -45,
                            format:
                                '<div style="text-align:center">' +
                                '<span style="font-size:25px; color: #7F7F7F;">{y}</span> &nbsp' +
                                '<span style="font-size:16px;color: #7F7F7F;">ppb</span>' +
                                '</div>'
                        },
						
                        tooltip: {
                            valueSuffix: 'ppb'
                        }
                    }] //end of series
 
                })); //chartTvoc
               //co2
               var co2Options = {
                       chart: {
                           type: 'solidgauge'
                       },
               
                       title: null,
               
                       pane: {
                           center: ['50%', '85%'],
                           size: '80%',
                           startAngle: -90,
                           endAngle: 90,
                           background: {
                               backgroundColor:
                                   Highcharts.defaultOptions.legend.backgroundColor || '#EEE',
                               innerRadius: '60%',
                               outerRadius: '100%',
                               shape: 'arc'
                           }
                       },
               
                       exporting: {
                           enabled: false
                       },
               
                       tooltip: {
                           enabled: false
                       },
               
                       // the value axis
                       yAxis: {
                           
                           stops: [
                               [0.27, '#79D2FF'], // skyblue
                               [0.33, '#79BE00'], // green
                               [0.67, '#FEA040'], // yellow
                               [1, '#FD5344'], // red
                           ],
						   lineWidth: 0,
                           tickWidth: 0,
                           minorTickInterval: null,
                           tickWidth: 0,
                           endOnTick: false,
                           maxPadding: 0,
                           title: {
                               y: -110
                           },
                           labels: {
                               y: 26.5
                           }
                       },
               
                       plotOptions: {
                           solidgauge: {
                               dataLabels: {
                                   y: 5,
                                   borderWidth: 0,
                                   useHTML: true
                               }
                           }
                       }
                   }; //end of tvocOptions

               // The Co2 gauge
               var chartCo2 = Highcharts.chart('co2', Highcharts.merge(co2Options, {
                   yAxis: {
                       min: 400,
                       max: 3200,
					   tickPositioner: function() {
                           return [this.min, this.max];
                         },
                       title: {
                           text: '이산화탄소(Co2)',
                           style: {
                               color: '#7F7F7F',
                               fontSize: '30px',
                               }
                       }
                   },

                   credits: {
                       enabled: false
                   },

                   series: [{
                       name: '이산화탄소(Co2)',
                       data: [ res.data[660].Co2*1000/1000],
                       dataLabels: {
                    	   y : -45,
                           format:
                               '<div style="text-align:center">' +
                               '<span style="font-size:25px; color: #7F7F7F;">{y}</span> &nbsp' +
                               '<span style="font-size:16px;color: #7F7F7F;">ppm</span>' +
                               '</div>'
                       },
                       tooltip: {
                           valueSuffix: 'ppm'
                       }
                   }] //end of series

               })); //chartCo2
               
            
    // The Co2 gauge
    var chartCo2 = Highcharts.chart('co2_m', Highcharts.merge(co2Options, {
        yAxis: {
            min: 400,
            max: 3200,
			tickPositioner: function() {
                    	   return [this.min, this.max];
                    	 },
            title: {
                text: '이산화탄소(Co2)',
                style: {
                    color: '#7F7F7F',
                    fontSize: '30px',
                    }
            }
        },

        credits: {
            enabled: false
        },

        series: [{
            name: '이산화탄소(Co2)',
            data: [ res.data[660].Co2*1000/1000],
            dataLabels: {
                    	   y : -45,
                           format:
                               '<div style="text-align:center">' +
                               '<span style="font-size:25px; color: #7F7F7F;">{y}</span> &nbsp' +
                               '<span style="font-size:16px;color: #7F7F7F;">ppm</span>' +
                               '</div>'
                       },
            tooltip: {
                valueSuffix: 'ppm'
            }
        }] //end of series

    })); //chartCo2
    



             //24hours linechart
            //temperature
            Highcharts.chart('temperature', {
                chart: {
                    type: 'spline',
                    height:'130%',
                    scrollablePlotArea: {
                        minWidth: 100,
                        scrollPositionX: 1
                    }
                },
                credits: {
                    enabled: false //하이차트 로고
                },
                title: {
                    text: '',
                    align: 'center'
                },
                xAxis: {
                    type: 'datetime',
                    categories: [
                        res.data[0].myreport.substring(11,16),
                        res.data[30].myreport.substring(11,16),
                        res.data[60].myreport.substring(11,16),
                        res.data[90].myreport.substring(11,16),
                        res.data[120].myreport.substring(11,16),
                        res.data[150].myreport.substring(11,16),
                        res.data[180].myreport.substring(11,16),
                        res.data[210].myreport.substring(11,16),
                        res.data[240].myreport.substring(11,16),
                        res.data[270].myreport.substring(11,16),
                        res.data[300].myreport.substring(11,16),
                        res.data[330].myreport.substring(11,16),
                        res.data[360].myreport.substring(11,16),
                        res.data[390].myreport.substring(11,16),
                        res.data[420].myreport.substring(11,16),
                        res.data[450].myreport.substring(11,16),
                        res.data[480].myreport.substring(11,16),
                        res.data[510].myreport.substring(11,16),
                        res.data[540].myreport.substring(11,16),
                        res.data[570].myreport.substring(11,16),
                        res.data[600].myreport.substring(11,16),
                        res.data[630].myreport.substring(11,16),
                        res.data[660].myreport.substring(11,16),
                        //res.data[690].myreport.substring(11,16),
                        ]
                }, //xAis
                yAxis: {
                    max: 100,
                    title: {
                        text: '°C'
                    },
                    startOnTick: true,//false,
                    minPadding: 10,
                    maxPadding: 4,
                    minorGridLineWidth: 0,
                    gridLineWidth: 0,
                    alternateGridColor: null,
                    plotBands: [
                        {
                        from: -20,
                        to: 25,
                        color: 'rgba(68, 170, 213, 0.1)',
                        label: {
                            //text: '추움',
                            style: {
                                color: '#606060'
                            }
                        }
                    }, 
                    { 
                        from: 25,
                        to: 50,
                        color: 'rgba(0, 0, 0, 0)',
                        label: {
                            //text: '따뜻함',
                            style: {
                                color: '#606060'
                            }
                        }
                    }, 
                    { 
                        from: 50,
                        to: 75,
                        color: 'rgba(68, 170, 213, 0.1)',
                        label: {
                            //text: '더움',
                            style: {
                                color: '#606060'
                            }
                        }
                    },
                    { 
                        from: 75,
                        to: 100,
                        color: 'rgba(0, 0, 0, 0)',
                        label: {
                            //text: '따뜻함',
                            style: {
                                color: '#606060'
                            }
                        }
                    },
                    ] //plotBands,
                   
                    
                }, //yAxis
                
                tooltip: {
                    valueSuffix: '°C'
                },
                plotOptions: {
                    spline: {
                        lineWidth: 4,
                        states: {
                            hover: {
                                lineWidth: 5
                            }
                        },
                        marker: {
                            enabled: true
                        },
                        //pointInterval: 3600000, // one hour
                        pointEnd: <%=startDate%>
                    },
                    connectNulls : true
                },
                series: [{
                    name: '온도',
					fontsize: '16px',
                    color: '#5551FF',
                    data: [
                        res.data[0].Temperature*1000/1000, 
                        res.data[30].Temperature*1000/1000,
                        res.data[60].Temperature*1000/1000, 
                        res.data[90].Temperature*1000/1000, 
                        res.data[120].Temperature*1000/1000, 
                        res.data[150].Temperature*1000/1000, 
                        res.data[180].Temperature*1000/1000, 
                        res.data[210].Temperature*1000/1000,
                        res.data[240].Temperature*1000/1000,
                        res.data[270].Temperature*1000/1000,
                        res.data[300].Temperature*1000/1000,
                        res.data[330].Temperature*1000/1000,
                        res.data[360].Temperature*1000/1000,
                        res.data[390].Temperature*1000/1000,
                        res.data[420].Temperature*1000/1000,
                        res.data[450].Temperature*1000/1000,
                        res.data[480].Temperature*1000/1000,
                        res.data[510].Temperature*1000/1000,
                        res.data[540].Temperature*1000/1000,
                        res.data[570].Temperature*1000/1000,
                        res.data[600].Temperature*1000/1000,
                        res.data[630].Temperature*1000/1000,
                        res.data[660].Temperature*1000/1000,
                        //res.data[690].Temperature*1000/1000,
                    ]

                }],
                navigation: {
                    menuItemStyle: {
                        fontSize: '10px'
                    }
                } //navigation
            });
            
                        //temperature
                        Highcharts.chart('temperature_m', {
                            chart: {
                                type: 'spline',
                                height:'130%',
                                scrollablePlotArea: {
                                    minWidth: 100,
                                    scrollPositionX: 1
                                }
                            },
                            credits: {
                                enabled: false //하이차트 로고
                            },
                            title: {
                                text: '',
                                align: 'center'
                            },
                            xAxis: {
                                type: 'datetime',
                                categories: [
                                    res.data[0].myreport.substring(11,16),
                                    res.data[30].myreport.substring(11,16),
                                    res.data[60].myreport.substring(11,16),
                                    res.data[90].myreport.substring(11,16),
                                    res.data[120].myreport.substring(11,16),
                                    res.data[150].myreport.substring(11,16),
                                    res.data[180].myreport.substring(11,16),
                                    res.data[210].myreport.substring(11,16),
                                    res.data[240].myreport.substring(11,16),
                                    res.data[270].myreport.substring(11,16),
                                    res.data[300].myreport.substring(11,16),
                                    res.data[330].myreport.substring(11,16),
                                    res.data[360].myreport.substring(11,16),
                                    res.data[390].myreport.substring(11,16),
                                    res.data[420].myreport.substring(11,16),
                                    res.data[450].myreport.substring(11,16),
                                    res.data[480].myreport.substring(11,16),
                                    res.data[510].myreport.substring(11,16),
                                    res.data[540].myreport.substring(11,16),
                                    res.data[570].myreport.substring(11,16),
                                    res.data[600].myreport.substring(11,16),
                                    res.data[630].myreport.substring(11,16),
                                    res.data[660].myreport.substring(11,16),
                                    //res.data[690].myreport.substring(11,16),
                                    ]
                            }, //xAis
                            yAxis: {
                                max: 100,
                                title: {
                                    text: '°C'
                                },
                                startOnTick: true,//false,
                                minPadding: 10,
                                maxPadding: 4,
                                minorGridLineWidth: 0,
                                gridLineWidth: 0,
                                alternateGridColor: null,
                                plotBands: [
                                    {
                                    from: -20,
                                    to: 25,
                                    color: 'rgba(68, 170, 213, 0.1)',
                                    label: {
                                        //text: '추움',
                                        style: {
                                            color: '#606060'
                                        }
                                    }
                                }, 
                                { 
                                    from: 25,
                                    to: 50,
                                    color: 'rgba(0, 0, 0, 0)',
                                    label: {
                                        //text: '따뜻함',
                                        style: {
                                            color: '#606060'
                                        }
                                    }
                                }, 
                                { 
                                    from: 50,
                                    to: 75,
                                    color: 'rgba(68, 170, 213, 0.1)',
                                    label: {
                                        //text: '더움',
                                        style: {
                                            color: '#606060'
                                        }
                                    }
                                },
                                { 
                                    from: 75,
                                    to: 100,
                                    color: 'rgba(0, 0, 0, 0)',
                                    label: {
                                        //text: '따뜻함',
                                        style: {
                                            color: '#606060'
                                        }
                                    }
                                },
                                ] //plotBands,
                               
                                
                            }, //yAxis
                            
                            tooltip: {
                                valueSuffix: '°C'
                            },
                            plotOptions: {
                                spline: {
                                    lineWidth: 4,
                                    states: {
                                        hover: {
                                            lineWidth: 5
                                        }
                                    },
                                    marker: {
                                        enabled: true
                                    },
                                    pointEnd: <%=startDate%>
                                },
                                connectNulls : true
                            },
                            series: [{
                                name: '온도',
								fontsize: '16px',
								color: '#5551FF',
                                data: [
                                    res.data[0].Temperature*1000/1000, 
                                    res.data[30].Temperature*1000/1000,
                                    res.data[60].Temperature*1000/1000, 
                                    res.data[90].Temperature*1000/1000, 
                                    res.data[120].Temperature*1000/1000, 
                                    res.data[150].Temperature*1000/1000, 
                                    res.data[180].Temperature*1000/1000, 
                                    res.data[210].Temperature*1000/1000,
                                    res.data[240].Temperature*1000/1000,
                                    res.data[270].Temperature*1000/1000,
                                    res.data[300].Temperature*1000/1000,
                                    res.data[330].Temperature*1000/1000,
                                    res.data[360].Temperature*1000/1000,
                                    res.data[390].Temperature*1000/1000,
                                    res.data[420].Temperature*1000/1000,
                                    res.data[450].Temperature*1000/1000,
                                    res.data[480].Temperature*1000/1000,
                                    res.data[510].Temperature*1000/1000,
                                    res.data[540].Temperature*1000/1000,
                                    res.data[570].Temperature*1000/1000,
                                    res.data[600].Temperature*1000/1000,
                                    res.data[630].Temperature*1000/1000,
                                    res.data[660].Temperature*1000/1000,
                                    //res.data[690].Temperature*1000/1000,
                                ]
            
                            }],
                            navigation: {
                                menuItemStyle: {
                                    fontSize: '10px'
                                }
                            } //navigation
                        });
            
            //습도
            Highcharts.chart('humid', {
                chart: {
                    type: 'spline',
                    height:'130%',
                    scrollablePlotArea: {
                        minWidth: 100,
                        minHeight:100,
                        scrollPositionX: 1
                    }
                },
                title: {
                    text: '',
                    align: 'center'
                },
                credits: {
                    enabled: false //하이차트 로고
                },
                xAxis: {
                    type: 'datetime',
                    categories: [
                        res.data[0].myreport.substring(11,16),
                        res.data[30].myreport.substring(11,16),
                        res.data[60].myreport.substring(11,16),
                        res.data[90].myreport.substring(11,16),
                        res.data[120].myreport.substring(11,16),
                        res.data[150].myreport.substring(11,16),
                        res.data[180].myreport.substring(11,16),
                        res.data[210].myreport.substring(11,16),
                        res.data[240].myreport.substring(11,16),
                        res.data[270].myreport.substring(11,16),
                        res.data[300].myreport.substring(11,16),
                        res.data[330].myreport.substring(11,16),
                        res.data[360].myreport.substring(11,16),
                        res.data[390].myreport.substring(11,16),
                        res.data[420].myreport.substring(11,16),
                        res.data[450].myreport.substring(11,16),
                        res.data[480].myreport.substring(11,16),
                        res.data[510].myreport.substring(11,16),
                        res.data[540].myreport.substring(11,16),
                        res.data[570].myreport.substring(11,16),
                        res.data[600].myreport.substring(11,16),
                        res.data[630].myreport.substring(11,16),
                        res.data[660].myreport.substring(11,16),
                        //res.data[690].myreport.substring(11,16),
                        ]
                }, //xAis
                yAxis: {
                    max: 100,
                    title: {
                        text: '%'
                    },
                    startOnTick: false,
                    minPadding: 10,
                    maxPadding: 10,//2,
                    minorGridLineWidth: 0,
                    gridLineWidth: 0,
                    alternateGridColor: null,
                    plotBands: [
                        {
                        from: 0,
                        to: 25,
                        color: 'rgba(68, 170, 213, 0.1)',
                        label: {
                            //text: '추움',
                            style: {
                                color: '#606060'
                            }
                        }
                    }, 
                    { 
                        from: 25,
                        to: 50,
                        color: 'rgba(0, 0, 0, 0)',
                        label: {
                            //text: '따뜻함',
                            style: {
                                color: '#606060'
                            }
                        }
                    }, 
                    { 
                        from: 50,
                        to: 75,
                        color: 'rgba(68, 170, 213, 0.1)',
                        label: {
                            //text: '더움',
                            style: {
                                color: '#606060'
                            }
                        }
                    },
                    { 
                        from: 75,
                        to: 100,
                        color: 'rgba(0, 0, 0, 0)',
                        label: {
                            //text: '따뜻함',
                            style: {
                                color: '#606060'
                            }
                        }
                    },
                    ] //plotBands,
                }, //yAxis
                
                tooltip: {
                    valueSuffix: '%'
                },
                plotOptions: {
                    spline: {
                        lineWidth: 4,
                        states: {
                            hover: {
                                lineWidth: 5
                            }
                        },
                        marker: {
                            enabled: true
                        },
                        pointEnd: <%=startDate%>
                    },
                    connectNulls : true
                },
                series: [{
                    name: '습도',
					fontsize: '16px',
                    color: '#5551FF',
                    data: [
                        res.data[0].Humid*1000/1000, 
                        res.data[30].Humid*1000/1000,
                        res.data[60].Humid*1000/1000, 
                        res.data[90].Humid*1000/1000, 
                        res.data[120].Humid*1000/1000, 
                        res.data[150].Humid*1000/1000, 
                        res.data[180].Humid*1000/1000, 
                        res.data[210].Humid*1000/1000,
                        res.data[240].Humid*1000/1000,
                        res.data[270].Humid*1000/1000,
                        res.data[300].Humid*1000/1000,
                        res.data[330].Humid*1000/1000,
                        res.data[360].Humid*1000/1000,
                        res.data[390].Humid*1000/1000,
                        res.data[420].Humid*1000/1000,
                        res.data[450].Humid*1000/1000,
                        res.data[480].Humid*1000/1000,
                        res.data[510].Humid*1000/1000,
                        res.data[540].Humid*1000/1000,
                        res.data[570].Humid*1000/1000,
                        res.data[600].Humid*1000/1000,
                        res.data[630].Humid*1000/1000,
                        res.data[660].Humid*1000/1000,
                        //res.data[690].Humid*1000/1000,
                    ]

                }],
                navigation: {
                    menuItemStyle: {
                        fontSize: '20px'
                    }
                }
            });

             //습도 모바일 전용뷰
             Highcharts.chart('humid_m', {
                chart: {
                    type: 'spline',
                    height:'130%',
                    scrollablePlotArea: {
                        minWidth: 100,
                        minHeight:100,
                        scrollPositionX: 1
                    }
                },
                title: {
                    text: '',
                    align: 'center'
                },
                credits: {
                    enabled: false //하이차트 로고
                },
                xAxis: {
                    type: 'datetime',
                    categories: [
                        res.data[0].myreport.substring(11,16),
                        res.data[30].myreport.substring(11,16),
                        res.data[60].myreport.substring(11,16),
                        res.data[90].myreport.substring(11,16),
                        res.data[120].myreport.substring(11,16),
                        res.data[150].myreport.substring(11,16),
                        res.data[180].myreport.substring(11,16),
                        res.data[210].myreport.substring(11,16),
                        res.data[240].myreport.substring(11,16),
                        res.data[270].myreport.substring(11,16),
                        res.data[300].myreport.substring(11,16),
                        res.data[330].myreport.substring(11,16),
                        res.data[360].myreport.substring(11,16),
                        res.data[390].myreport.substring(11,16),
                        res.data[420].myreport.substring(11,16),
                        res.data[450].myreport.substring(11,16),
                        res.data[480].myreport.substring(11,16),
                        res.data[510].myreport.substring(11,16),
                        res.data[540].myreport.substring(11,16),
                        res.data[570].myreport.substring(11,16),
                        res.data[600].myreport.substring(11,16),
                        res.data[630].myreport.substring(11,16),
                        res.data[660].myreport.substring(11,16),
                        //res.data[690].myreport.substring(11,16),
                        ]
                }, //xAis
                yAxis: {
                    max: 100,
                    title: {
                        text: '%'
                    },
                    startOnTick: false,
                    minPadding: 10,
                    maxPadding: 10,//2,
                    minorGridLineWidth: 0,
                    gridLineWidth: 0,
                    alternateGridColor: null,
                    plotBands: [
                        {
                        from: 0,
                        to: 25,
                        color: 'rgba(68, 170, 213, 0.1)',
                        label: {
                            //text: '추움',
                            style: {
                                color: '#606060'
                            }
                        }
                    }, 
                    { 
                        from: 25,
                        to: 50,
                        color: 'rgba(0, 0, 0, 0)',
                        label: {
                            //text: '따뜻함',
                            style: {
                                color: '#606060'
                            }
                        }
                    }, 
                    { 
                        from: 50,
                        to: 75,
                        color: 'rgba(68, 170, 213, 0.1)',
                        label: {
                            //text: '더움',
                            style: {
                                color: '#606060'
                            }
                        }
                    },
                    { 
                        from: 75,
                        to: 100,
                        color: 'rgba(0, 0, 0, 0)',
                        label: {
                            //text: '따뜻함',
                            style: {
                                color: '#606060'
                            }
                        }
                    },
                    ] //plotBands,
                }, //yAxis
                
                tooltip: {
                    valueSuffix: '%'
                },
                plotOptions: {
                    spline: {
                        lineWidth: 4,
                        states: {
                            hover: {
                                lineWidth: 5
                            }
                        },
                        marker: {
                            enabled: true
                        },
                        pointEnd: <%=startDate%>
                    },
                    connectNulls : true
                },
                series: [{
                    name: '습도',
					fontsize: '16px',
                    color: '#5551FF',
                    data: [
                        res.data[0].Humid*1000/1000, 
                        res.data[30].Humid*1000/1000,
                        res.data[60].Humid*1000/1000, 
                        res.data[90].Humid*1000/1000, 
                        res.data[120].Humid*1000/1000, 
                        res.data[150].Humid*1000/1000, 
                        res.data[180].Humid*1000/1000, 
                        res.data[210].Humid*1000/1000,
                        res.data[240].Humid*1000/1000,
                        res.data[270].Humid*1000/1000,
                        res.data[300].Humid*1000/1000,
                        res.data[330].Humid*1000/1000,
                        res.data[360].Humid*1000/1000,
                        res.data[390].Humid*1000/1000,
                        res.data[420].Humid*1000/1000,
                        res.data[450].Humid*1000/1000,
                        res.data[480].Humid*1000/1000,
                        res.data[510].Humid*1000/1000,
                        res.data[540].Humid*1000/1000,
                        res.data[570].Humid*1000/1000,
                        res.data[600].Humid*1000/1000,
                        res.data[630].Humid*1000/1000,
                        res.data[660].Humid*1000/1000,
                        //res.data[690].Humid*1000/1000,
                    ]

                }],
                navigation: {
                    menuItemStyle: {
                        fontSize: '20px'
                    }
                }
            });

    });  //end of ajex 


}); // end of document ready


</script>

<script> 
function chageLangSelect(){ 
	var langSelect = document.getElementById("selectbox"); 
	// select element에서 선택된 option의 value가 저장된다. 
	var selectValue = langSelect.options[langSelect.selectedIndex].value; 
	// select element에서 선택된 option의 text가 저장된다. 
	var selectText = langSelect.options[langSelect.selectedIndex].text; 
} 
</script>



</head>


<body onload="showClock()">
    
    <form class="layer" height="80px" style="margin: 30px;">
    
    <fieldset align="center">
    <select id="selectbox"" name="selectbox" onchange="location.href=this.value">

                            <option value="/mqtt/Page.jsp">선택</option>
                            <option value="/mqtt/Total.jsp?serialNum=240AC4223E8E">극동메이저1</option>
                            <option value="/mqtt/Total.jsp?serialNum=AC67B25CC212">극동메이저2</option>
							<option value="/mqtt/Total.jsp?serialNum=240AC422386A">부천대1</option>
                            <option value="/mqtt/Total.jsp?serialNum=AC67B25CC502">브릴컴사무실</option>
   </select> 
     
   &nbsp;
        <label>맥어드레스 :</label>
        <input style="margin: 30px 0px 0px 0px;" type="text" name="serialNum" value="<%=insertNum %>" />
        <input type="submit" value="검색" />
        <div style="margin: 30px 0px 0px 0px;" id="divClock" class="clock"></div>
		<img id="logo" alt="logo" src="1024_1024_logo.png" width="80px" height="80px" align="left">
    </fieldset>
</form>
    
    <table class="responsive_window">
      <tr>
        <td rowspan='2'><div id="container"></div></td>
        <td rowspan='2'><div id="container2"></div></td>
        <td><div id="pm25" class="chart-container"></div></td>
      </tr>
      <tr>
        <!--<td>1</td>-->
        <td><div id="pm10" class="chart-container"></div></td>
      </tr>
      <tr>
        <td rowspan='2'><div id="temperature"></div></td>
        <td rowspan='2'><div id="humid"></div></td>
        <td><div id="tvoc" class="chart-container"></div></td>
      </tr>
      <tr>
        <!--<td>1</td>-->
        <td><div id="co2" class="chart-container"></div></td>
      </tr>


    </table>


    <table class="responsive_mobile">
        <tr>
            <td><div id="container3"></div></td>
          </tr>
          <tr>
              <td><div id="container4"></div></td>
          </tr>
		  <tr>
            <td><div id="temperature_m"></div></td>
          </tr>
		  <tr>
              <td><div id="humid_m"></div></td>
          </tr>
          <tr>
              <td><div id="pm25_m" class="chart-container"></div></td>
          </tr>
          <tr>
            <!--<td>1</td>-->
            <td><div id="pm10_m" class="chart-container"></div></td>
          </tr>
          <tr>
              <td><div id="tvoc_m" class="chart-container"></div></td>
          </tr>
    
          <tr>
            <!--<td>1</td>-->
            <td><div id="co2_m" class="chart-container"></div></td>
          </tr>
      </table>

    
<div id="coName">Sourced by Brilliant and Company Co., Ltd</div>

</body>
</html>