define(["./when-54c2dc71","./Check-6c0211bc","./Math-fc8cecf5","./Cartesian2-bddc1162","./Transforms-43808565","./RuntimeError-2109023a","./WebGLConstants-76bb35d1","./ComponentDatatype-6d99a1ee","./GeometryAttribute-49698167","./GeometryAttributes-4fcfcf40","./IndexDatatype-53503fee","./GeometryOffsetAttribute-7350d9af","./VertexFormat-7572c785","./EllipsoidGeometry-46c9ce62"],function(a,e,t,o,r,i,n,s,c,d,l,m,u,p){"use strict";function y(e){var t=a.defaultValue(e.radius,1),r={radii:new o.Cartesian3(t,t,t),stackPartitions:e.stackPartitions,slicePartitions:e.slicePartitions,vertexFormat:e.vertexFormat};this._ellipsoidGeometry=new p.EllipsoidGeometry(r),this._workerName="createSphereGeometry"}y.packedLength=p.EllipsoidGeometry.packedLength,y.pack=function(e,t,r){return p.EllipsoidGeometry.pack(e._ellipsoidGeometry,t,r)};var f=new p.EllipsoidGeometry,G={radius:void 0,radii:new o.Cartesian3,vertexFormat:new u.VertexFormat,stackPartitions:void 0,slicePartitions:void 0};return y.unpack=function(e,t,r){var i=p.EllipsoidGeometry.unpack(e,t,f);return G.vertexFormat=u.VertexFormat.clone(i._vertexFormat,G.vertexFormat),G.stackPartitions=i._stackPartitions,G.slicePartitions=i._slicePartitions,a.defined(r)?(o.Cartesian3.clone(i._radii,G.radii),r._ellipsoidGeometry=new p.EllipsoidGeometry(G),r):(G.radius=i._radii.x,new y(G))},y.createGeometry=function(e){return p.EllipsoidGeometry.createGeometry(e._ellipsoidGeometry)},function(e,t){return a.defined(t)&&(e=y.unpack(e,t)),y.createGeometry(e)}});
