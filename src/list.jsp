<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="com.brilcom.ctr.GetAirQualityResponse, java.util.List"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

<script type="text/javascript">
    window.onload = function() {

       new Chart(document.getElementById("pm25"), {
            type : 'line',
            data : {
                labels :  [<%=("selectDate")%>],
                datasets : [ {
                    data : [<%=("pm25")%>],
                    label : "pm2.5",
                    borderColor : "#3e95cd",
                    fill : false
                } ]
            },
            options : {
                title : {
                    display : true,
                    text : 'pm2.5'
                }
            }
        });
       console.log(rs.getString("pm25"));
       
       <%--        const ctx = document.getElementById('pm25').getContext('2d');
       const pm25 = new Chart(ctx, {
           type : 'line',
           data : {
               labels : [<%=rs.getString("selectDate")%> ],
               datasets : [ {
                   data : [
                            <%=rs.getString("pm25")%>
   ],
                   label : "pm25",
                   borderColor : "#808080",
                   fill : false,
                   showLine : true,
                   spanGaps : true
               } ]
           },
           options : {
               title : {
                   display : true,
                   text : 'pm25'
               }
           },
           labels :{
               title : {
                   display : true,
                   text : 'pm25'
               }
           }
       }); --%>
      <%--  console.log(<%=rs.getString("pm25")%>);
        
        //pm10
        new Chart(document.getElementById("pm10"), {
            type : 'line',
            data : {
                labels : [<%=rs.getString("selectDate")%> ],
                datasets : [ {
                    data : [
<%=rs.getString("pm10")%>
    ],
                    label : "pm10",
                    borderColor : "#ff0000",
                    fill : false
                } ]
            },
            options : {
                title : {
                    display : true,
                    text : 'pm10'
                }
            },
            labels :{
                title : {
                    display : true,
                    text : ''
                }
            }
        });
        console.log(<%=rs.getString("pm10")%>);

        //temperature
        new Chart(document.getElementById("temperature"), {
            type : 'line',
            data : {
                labels : [<%=rs.getString("selectDate")%> ],
                datasets : [ {
                    data : [
<%=rs.getString("temperature")%>
    ],
                    label : "temperature",
                    borderColor : "#99a000",
                    fill : false
                } ]
            },
            options : {
                title : {
                    display : true,
                    text : 'temperature'
                }
            }
        });

        //humid
        new Chart(document.getElementById("humid"), {
            type : 'line',
            data : {
                labels : [<%=rs.getString("selectDate")%> ],
                datasets : [ {
                    data : [
<%=rs.getString("humid")%>
    ],
                    label : "humid",
                    borderColor : "#ffb871",
                    fill : false
                } ]
            },
            options : {
                title : {
                    display : true,
                    text : 'humid'
                }
            }
        });

        //co2
        new Chart(document.getElementById("co2"), {
            type : 'line',
            data : {
                labels : [<%=rs.getString("selectDate")%> ],
                datasets : [ {
                    data : [
<%=rs.getString("co2")%>
    ],
                    label : "co2",
                    borderColor : "#9952ff",
                    fill : false
                } ]
            },
            options : {
                title : {
                    display : true,
                    text : 'co2'
                }
            }
        });

        //tvoc
        new Chart(document.getElementById("tvoc"), {
            type : 'line',
            data : {
                labels : [<%=rs.getString("selectDate")%> ],
                datasets : [ {
                    data : [
<%=rs.getString("tvoc")%>
    ],
                    label : "tvoc",
                    borderColor : "#0b1d6b",
                    fill : false
                } ]
            },
            options : {
                title : {
                    display : true,
                    text : 'tvoc'
                }
            }

        })
        
    };
 --%>

</script>




</body>
</html>