package com.brandonjamesyoung.levelup.constants

import org.intellij.lang.annotations.Language

@Language("AGSL")
const val BASIC_COLOR_SHADER_SRC = """
    uniform float2 iResolution;
    layout(color) uniform half4 iColor;
    
    half4 main(float2 fragCoord) {
        return iColor;
    }
"""

@Language("AGSL")
const val HISTORY_CARD_SHADER_SRC = """
    uniform float2 iResolution;
    layout(color) uniform half4 iColor;
    
    half4 main(float2 fragCoord) {
        float2 st = fragCoord/iResolution.xy;
        float PI = 3.14159;
        
        // From 0.0 - 1.0, gets darker the closer the value is to 0.0
        float darkness_factor = 0.8;
        
        float3 color_xyz = float3(iColor.x, iColor.y, iColor.z);
        float3 color_gap = color_xyz;
        float3 color_stripe = color_xyz * darkness_factor;
        
        // Doesn't exactly correspond to how many stripes will appear due to resolution
        float num_stripes = 4.0;
        
        // 1.0 means no stripes, 2.0 means stripes and gaps are equal length
        float stripe_bias = 3.0; 
        
        float angle = PI/4.0 + PI/2.0;
    
        float3 outputColor;
        float w = cos(angle) * st.x + sin(angle) * st.y;
        
        if (floor(mod(w * num_stripes, stripe_bias)) < 0.0001) {
            outputColor = color_gap;
        } else {
            outputColor = color_stripe;
        }
   
        return half4(outputColor,1.0);
    }
"""