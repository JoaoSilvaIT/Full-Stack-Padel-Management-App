
import {renderRegisterView} from "../views/userRegister.js";

export default{
    path:"users/register",
    handler:(content)=>{
        const element = renderRegisterView()
        content.replaceChildren(element)
    }
}