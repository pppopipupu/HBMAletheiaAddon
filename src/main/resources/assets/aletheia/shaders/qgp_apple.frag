#version 120

uniform sampler2D texture;
uniform sampler2D u_screenTexture;
uniform float iTime;
uniform float u_screenWidth;
uniform float u_screenHeight;
uniform int u_hasFbo;
uniform vec4 u_iconUvRange;

float hash(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

void main() {
    vec2 uv = gl_TexCoord[0].st;

    vec4 origTexColor = vec4(0.0);
    if (uv.x >= 0.0 && uv.x <= 1.0 && uv.y >= 0.0 && uv.y <= 1.0) {
        vec2 iconGlobalUv = u_iconUvRange.xz + uv * (u_iconUvRange.yw - u_iconUvRange.xz);
        origTexColor = texture2D(texture, iconGlobalUv);
    }

    if (origTexColor.a < 0.05) {
        discard;
    }

    vec2 center = vec2(0.5, 0.5);
    vec2 pos = uv - center;
    float dist = length(pos);
    float angle = atan(pos.y, pos.x);

    float t = iTime * 2.0;
    float hue = angle / 6.28318530718 + t * 0.2;
    vec3 rainbow = 0.5 + 0.5 * cos(6.28318530718 * (hue + vec3(0.0, 0.33, 0.67)));

    float pulse = 0.7 + 0.3 * sin(t * 3.0 - dist * 8.0);
    float halo = exp(-dist * 4.0) * 1.5;

    vec2 starPos = gl_FragCoord.xy * 0.3;
    vec2 ipos = floor(starPos);
    vec2 fpos = fract(starPos);
    float h = hash(ipos);
    float star = 0.0;
    if (h > 0.85) {
        float sf = 0.5 + 0.5 * sin(t * 8.0 + h * 6.28);
        star = smoothstep(0.15, 0.0, length(fpos - vec2(0.5))) * sf * 1.2;
    }

    vec3 auraColor = rainbow * pulse * (1.0 + halo) + vec3(star);

    vec3 finalColor;
    if (u_hasFbo == 1) {
        vec2 screenUv = gl_FragCoord.xy / vec2(u_screenWidth, u_screenHeight);
        vec3 background = texture2D(u_screenTexture, clamp(screenUv, vec2(0.001), vec2(0.999))).rgb;
        finalColor = mix(background, auraColor, origTexColor.a * 0.65);
        finalColor += rainbow * 0.3 * pulse;
    } else {
        finalColor = mix(origTexColor.rgb, auraColor, 0.6);
    }

    gl_FragColor = vec4(finalColor, origTexColor.a);
}
