package com.imu.flowerdelivery.network.interfaces

import app.kevs.treeview.network.models.*
import com.imu.flowerdelivery.network.models.*
import retrofit2.Call
import retrofit2.http.*

interface TreeApi {
    /** GET **/
    @GET("nodes/{project}")
    fun getAllNodes(@Path("project") project: String): Call<Array<NodeDto>>

    @GET("nodesbyuser/{user}")
    fun getAllNodesByUser(@Path("user") user: String): Call<Array<NodeDto>>

    @GET("projects/{user}")
    fun getProjectsForUser(@Path("user") user:String): Call<Array<Project>>

    @GET("nodesdelete/{project}")
    fun deleteNodesInProject(@Path("project") project: String): Call<Array<NodeDto>>

    @GET("nodesbranchdelete/{path}")
    fun deleteNodesInPath(@Path("path") path: String): Call<Array<NodeDto>>


    /** POST **/
    @POST("nodes")
    fun addNode(@Body nodeDto: NodeDto): Call<ResponseObject<NodeDto>>

    @POST("nodesupdate")
    fun updateNode(@Body updateDto: NodeUpdateDto): Call<ResponseObject<NodeDto>>

    @POST("nodesdelete")
    fun deleteNode(@Body nodeDto: NodeDto): Call<ResponseObject<NodeDto>>

    @POST("users/login")
    fun login(@Body loginModel: LoginModel): Call<ResponseObject<User>>

    @POST("users")
    fun register(@Body user: User): Call<ResponseObject<User>>

    @POST("projects")
    fun addProject(@Body project: Project): Call<ResponseObject<Project>>

    @POST("projectsdelete")
    fun deleteProject(@Body project: Project): Call<ResponseObject<Project>>

    /** PUT **/

    /** PATCH **/

    /** DELETE **/

}