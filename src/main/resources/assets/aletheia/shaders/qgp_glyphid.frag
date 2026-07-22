#version 120

uniform sampler2D texture;
uniform sampler2D u_screenTexture;
uniform float iTime;
uniform float time;
uniform float u_screenWidth;
uniform float u_screenHeight;
uniform int u_hasFbo;

float hash(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

void main() {
    vec2 uv = gl_TexCoord[0].st;
    vec4 texColor = texture2D(texture, uv);
    float t = (iTime > 0.0) ? iTime : time;

    vec2 screenCoord = gl_FragCoord.xy * 0.05 + uv * 5.0;
    float wave1 = sin(screenCoord.x * 1.5 + t * 4.0);
    float wave2 = cos(screenCoord.y * 1.5 - t * 3.0);
    float pulse = (wave1 + wave2) * 0.5 + 0.5;

    float r = 0.5 + 0.5 * sin(screenCoord.x * 0.8 + t * 3.0);
    float g = 0.5 + 0.5 * sin(screenCoord.y * 0.9 - t * 2.5 + 2.0);
    float b = 0.5 + 0.5 * sin((screenCoord.x - screenCoord.y) * 0.7 + t * 3.5 + 4.0);
    vec3 rainbowPlasma = vec3(r, g, b) * 1.6;

    vec3 colPurple  = vec3(0.7, 0.0, 1.0);
    vec3 colCyan    = vec3(0.0, 1.0, 0.9);
    vec3 colMagenta = vec3(1.0, 0.0, 0.6);

    vec3 plasmaMix = mix(colPurple, colCyan, sin(t * 3.0 + screenCoord.y * 2.0) * 0.5 + 0.5);
    plasmaMix = mix(plasmaMix, colMagenta, pulse * 0.8);

    vec2 starCoord = gl_FragCoord.xy * 0.3;
    vec2 ipos = floor(starCoord);
    vec2 fpos = fract(starCoord);
    float h = hash(ipos);
    float starGlow = 0.0;
    if (h > 0.88) {
        float starFlash = 0.5 + 0.5 * sin(t * 12.0 + h * 6.28);
        starGlow = smoothstep(0.2, 0.0, length(fpos - vec2(0.5))) * starFlash * 2.5;
    }

    vec3 emissivePlasma = mix(plasmaMix, rainbowPlasma, 0.7) * (1.2 + 0.4 * pulse) + vec3(starGlow);

    vec3 finalColor = texColor.rgb;
    if (u_hasFbo == 1) {
        vec2 screenUv = gl_FragCoord.xy / vec2(u_screenWidth, u_screenHeight);
        vec2 distortionOffset = vec2(
            sin(gl_FragCoord.y * 0.05 + t * 3.5),
            cos(gl_FragCoord.x * 0.05 - t * 3.0)
        ) * 0.035;
        vec2 sampleUv = clamp(screenUv + distortionOffset, vec2(0.001), vec2(0.999));
        vec3 distortedBg = texture2D(u_screenTexture, sampleUv).rgb;
        finalColor = mix(distortedBg, emissivePlasma, 0.5) * 1.2;
    } else {
        vec3 baseRgb = (texColor.a > 0.01) ? texColor.rgb : vec3(0.3, 0.0, 0.4);
        finalColor = mix(baseRgb, emissivePlasma, 0.85);
    }

    float edgeGlow = pow(pulse, 2.0) * 0.6;
    finalColor += vec3(edgeGlow * 0.7, edgeGlow * 1.0, edgeGlow * 1.4);

    gl_FragColor = vec4(finalColor, 1.0);
}
